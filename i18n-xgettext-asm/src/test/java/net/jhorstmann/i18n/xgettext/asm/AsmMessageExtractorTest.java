package net.jhorstmann.i18n.xgettext.asm;

import net.jhorstmann.i18n.I18N;
import net.jhorstmann.i18n.xgettext.MessageExtractorException;
import org.fedorahosted.tennera.jgettext.Message;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import net.jhorstmann.i18n.tools.MessageBundle;

public class AsmMessageExtractorTest {

    private static MessageBundle xgettext(Class<?> clazz) throws IOException, MessageExtractorException {
        MessageBundle bundle = new MessageBundle();

        AsmMessageExtractor extractor = new AsmMessageExtractor(bundle);
        InputStream in = clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class");
        try {
            extractor.extractMessages(in);
        } finally {
            in.close();
        }
        return bundle;
    }

    @Test
    public void message() throws IOException, MessageExtractorException {
        MessageBundle bundle = xgettext(new Object() {
            public void test() {
                I18N.tr("Hello World");
            }
        }.getClass());

        Assert.assertEquals(1, bundle.size());
        Message msg = bundle.getMessage("Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World", msg.getMsgid());
    }

    @Test
    public void messageWithContext() throws IOException, MessageExtractorException {
        MessageBundle bundle = xgettext(new Object() {
            public void test() {
                I18N.trc("Hello World (context)", "Hello World");
            }
        }.getClass());

        Assert.assertEquals(1, bundle.size());
        Message msg = bundle.getMessage("Hello World (context)", "Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World (context)", msg.getMsgctxt());
        Assert.assertEquals("Hello World", msg.getMsgid());
    }

    @Test
    public void messageWithPlural() throws IOException, MessageExtractorException {
        MessageBundle bundle = xgettext(new Object() {
            public void test() {
                long n = 42;
                I18N.trn("Hello World", "Hello Worlds", n);
            }
        }.getClass());

        Assert.assertEquals(1, bundle.size());
        Message msg = bundle.getMessage("Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World", msg.getMsgid());
        Assert.assertEquals("Hello Worlds", msg.getMsgidPlural());
    }

    @Test
    public void messageWithContextAndPlural() throws IOException, MessageExtractorException {
        MessageBundle bundle = xgettext(new Object() {
            public void test() {
                long n = 42;
                I18N.trnc("Hello World (context)", "Hello World", "Hello Worlds", n);
            }
        }.getClass());

        Assert.assertEquals(1, bundle.size());
        Message msg = bundle.getMessage("Hello World (context)", "Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World (context)", msg.getMsgctxt());
        Assert.assertEquals("Hello World", msg.getMsgid());
        Assert.assertEquals("Hello Worlds", msg.getMsgidPlural());
    }

    @Test
    public void staticFinalMessage() throws IOException, MessageExtractorException {
        MessageBundle bundle = xgettext(new Object() {
            static final String HELLO = "Hello World";
            public void test() {
                I18N.tr(HELLO);
            }
        }.getClass());

        Assert.assertEquals(1, bundle.size());
        Message msg = bundle.getMessage("Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World", msg.getMsgid());
    }

    @Test
    public void localVarMessage() throws IOException, MessageExtractorException {
        MessageBundle bundle = xgettext(new Object() {
            public void test() {
                String hello = "Hello World";
                I18N.tr(hello);
            }
        }.getClass());

        Assert.assertEquals(1, bundle.size());
        Message msg = bundle.getMessage("Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World", msg.getMsgid());
    }

    @Test
    public void concatMessage() throws IOException, MessageExtractorException {
        MessageBundle bundle = xgettext(new Object() {
            public void test() {
                String hello = "Hello" + " " + "World";
                I18N.tr(hello);
            }
        }.getClass());

        Assert.assertEquals(1, bundle.size());
        Message msg = bundle.getMessage("Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World", msg.getMsgid());
    }

    @Test
    public void concatClassNameMessage() throws IOException, MessageExtractorException {
        MessageBundle bundle = xgettext(new Object() {
            public void test() {
                I18N.tr("Hello ".concat(I18N.class.getSimpleName()));
            }
        }.getClass());

        Assert.assertEquals(1, bundle.size());
        Message msg = bundle.getMessage("Hello I18N");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello I18N", msg.getMsgid());
    }

}
