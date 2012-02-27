package net.jhorstmann.i18n.tools;

import java.io.File;
import java.io.IOException;
import junit.framework.Assert;
import org.fedorahosted.tennera.jgettext.Message;
import org.junit.Test;

public class MoParserTest {

    @Test
    public void testMoParser() throws IOException {
        MessageBundle bundle = MoParser.parseMessages(new File("src/test/resources/net/jhorstmann/i18n/tools/Test_de.mo"));
        Assert.assertNotNull(bundle);
        Assert.assertEquals(1, bundle.size());
        Message message = bundle.getMessage("test");
        Assert.assertNotNull(message);
        Assert.assertEquals("test", message.getMsgid());
        Assert.assertEquals("de", message.getMsgstr());
    }

    @Test
    public void testHeader() throws IOException {
        MessageBundle bundle = MoParser.parseMessages(new File("src/test/resources/net/jhorstmann/i18n/tools/Header.mo"));
        Assert.assertNotNull(bundle);
        Assert.assertEquals(1, bundle.size());
        Message message = bundle.getHeaderMessage();
        Assert.assertNotNull(message);
    }

    @Test
    public void testABC() throws IOException {
        MessageBundle bundle = MoParser.parseMessages(new File("src/test/resources/net/jhorstmann/i18n/tools/ABC.mo"));
        Assert.assertNotNull(bundle);
        Assert.assertEquals(3, bundle.size());
        {
            Message message = bundle.getMessage("a");
            Assert.assertNotNull(message);
            Assert.assertEquals("a", message.getMsgid());
            Assert.assertEquals("a", message.getMsgstr());
        }
        {
            Message message = bundle.getMessage("b");
            Assert.assertNotNull(message);
            Assert.assertEquals("b", message.getMsgid());
            Assert.assertEquals("b", message.getMsgstr());
        }
        {
            Message message = bundle.getMessage("c");
            Assert.assertNotNull(message);
            Assert.assertEquals("c", message.getMsgid());
            Assert.assertEquals("c", message.getMsgstr());
        }
    }
}
