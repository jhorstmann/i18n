package net.jhorstmann.i18n.tools;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;
import junit.framework.Assert;
import net.jhorstmann.i18n.GettextResourceBundle;
import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.HeaderUtil;
import org.fedorahosted.tennera.jgettext.Message;
import org.junit.Test;

public class ResourceBundleCompilerTest {

    static MessageBundle createBundle() {
        MessageBundle bundle = new MessageBundle();
        {
            Message msg = HeaderUtil.generateDefaultHeader();
            HeaderFields fields = HeaderFields.wrap(msg);
            fields.setValue("Plural-Forms", "nplurals=2; plural=n == 1 ? 0 : 1;");
            msg = fields.unwrap();
            bundle.addMessage(msg);
        }
        {
            Message msg = new Message();
            msg.setMsgctxt("ctx1");
            msg.setMsgid("id1");
            msg.setMsgstr("str1");
            bundle.addMessage(msg);
        }
        {
            Message msg = new Message();
            msg.setMsgctxt("ctx2");
            msg.setMsgid("id2");
            msg.setMsgidPlural("id2plural");
            msg.addMsgstrPlural("plural", 0);
            msg.addMsgstrPlural("plurals", 1);
            bundle.addMessage(msg);
        }

        return bundle;
    }

    @Test
    public void testCompileResourceBundle() throws InstantiationException, IllegalAccessException, NoSuchFieldException {
        MessageBundle bundle = createBundle();
        String parentClassName = GettextResourceBundle.class.getName();
        Class<ResourceBundle> bundleClass = ResourceBundleCompiler.compileAndLoad(bundle, parentClassName, "net.jhorstmann.i18n.tools.TestResourceBundle_de", getClass().getClassLoader());
        {
            Field messagesField = bundleClass.getDeclaredField("messages");
            messagesField.setAccessible(true);
            Map messages = (Map) messagesField.get(null);
            Assert.assertNotNull(messages);
            Assert.assertEquals(3, messages.size());
            Object str1 = messages.get("ctx1\u0004id1");
            Assert.assertEquals("str1", str1);
            Object str2 = messages.get("ctx2\u0004id2");
            Assert.assertTrue(str2 instanceof String[]);
            Assert.assertEquals(2, ((String[])str2).length);
            String singular = ((String[])str2)[0];
            String plural = ((String[])str2)[1];
            Assert.assertEquals("plural", singular);
            Assert.assertEquals("plurals", plural);
        }
        ResourceBundle rb = bundleClass.newInstance();
        {
            Assert.assertEquals("str1", GettextResourceBundle.pgettext(rb, "ctx1", "id1"));
            Assert.assertEquals("plurals", GettextResourceBundle.npgettext(rb, "ctx2", "id2", "id2plural", 0L));
            Assert.assertEquals("plural" , GettextResourceBundle.npgettext(rb, "ctx2", "id2", "id2plural", 1L));
            Assert.assertEquals("plurals", GettextResourceBundle.npgettext(rb, "ctx2", "id2", "id2plural", 2L));
            Assert.assertEquals("plurals", GettextResourceBundle.npgettext(rb, "ctx2", "id2", "id2plural", 3L));
        }
        {
            Enumeration<String> keys = rb.getKeys();
            Assert.assertTrue(keys.hasMoreElements());
            
        }
    }
}
