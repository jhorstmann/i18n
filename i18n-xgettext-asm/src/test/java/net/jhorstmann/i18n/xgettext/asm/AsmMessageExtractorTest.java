package net.jhorstmann.i18n.xgettext.asm;

import net.jhorstmann.i18n.I18N;
import net.jhorstmann.i18n.xgettext.MessageExtractorException;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.Message;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class AsmMessageExtractorTest {

    private static Catalog xgettext(Class<?> clazz) throws IOException, MessageExtractorException {
        Catalog catalog = new Catalog(true);

        AsmMessageExtractor extractor = new AsmMessageExtractor(catalog);
        InputStream in = clazz.getResourceAsStream("/" + clazz.getName().replace('.', '/') + ".class");
        try {
            extractor.extractMessages(in);
        } finally {
            in.close();
        }
        return catalog;
    }

    @Test
    public void message() throws IOException, MessageExtractorException {
        Catalog catalog = xgettext(new Object() {
            public void test() {
                I18N.tr("Hello World");
            }
        }.getClass());

        Assert.assertEquals(1, catalog.size());
        Message msg = catalog.locateMessage(null, "Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World", msg.getMsgid());
    }

    @Test
    public void messageWithContext() throws IOException, MessageExtractorException {
        Catalog catalog = xgettext(new Object() {
            public void test() {
                I18N.trc("Hello World (context)", "Hello World");
            }
        }.getClass());

        Assert.assertEquals(1, catalog.size());
        Message msg = catalog.locateMessage("Hello World (context)", "Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World (context)", msg.getMsgctxt());
        Assert.assertEquals("Hello World", msg.getMsgid());
    }

    @Test
    public void messageWithPlural() throws IOException, MessageExtractorException {
        Catalog catalog = xgettext(new Object() {
            public void test() {
                long n = 42;
                I18N.trn("Hello World", "Hello Worlds", n);
            }
        }.getClass());

        Assert.assertEquals(1, catalog.size());
        Message msg = catalog.locateMessage(null, "Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World", msg.getMsgid());
        Assert.assertEquals("Hello Worlds", msg.getMsgidPlural());
    }

    @Test
    public void messageWithContextAndPlural() throws IOException, MessageExtractorException {
        Catalog catalog = xgettext(new Object() {
            public void test() {
                long n = 42;
                I18N.trnc("Hello World (context)", "Hello World", "Hello Worlds", n);
            }
        }.getClass());

        Assert.assertEquals(1, catalog.size());
        Message msg = catalog.locateMessage("Hello World (context)", "Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World (context)", msg.getMsgctxt());
        Assert.assertEquals("Hello World", msg.getMsgid());
        Assert.assertEquals("Hello Worlds", msg.getMsgidPlural());
    }

    @Test
    public void staticFinalMessage() throws IOException, MessageExtractorException {
        Catalog catalog = xgettext(new Object() {
            static final String HELLO = "Hello World";
            public void test() {
                I18N.tr(HELLO);
            }
        }.getClass());

        Assert.assertEquals(1, catalog.size());
        Message msg = catalog.locateMessage(null, "Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World", msg.getMsgid());
    }

    @Test
    public void localVarMessage() throws IOException, MessageExtractorException {
        Catalog catalog = xgettext(new Object() {
            public void test() {
                String hello = "Hello World";
                I18N.tr(hello);
            }
        }.getClass());

        Assert.assertEquals(1, catalog.size());
        Message msg = catalog.locateMessage(null, "Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World", msg.getMsgid());
    }

    @Test
    public void concatMessage() throws IOException, MessageExtractorException {
        Catalog catalog = xgettext(new Object() {
            public void test() {
                String hello = "Hello" + " " + "World";
                I18N.tr(hello);
            }
        }.getClass());

        Assert.assertEquals(1, catalog.size());
        Message msg = catalog.locateMessage(null, "Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello World", msg.getMsgid());
    }

    @Test
    public void concatClassNameMessage() throws IOException, MessageExtractorException {
        Catalog catalog = xgettext(new Object() {
            public void test() {
                I18N.tr("Hello ".concat(Catalog.class.getSimpleName()));
            }
        }.getClass());

        Assert.assertEquals(1, catalog.size());
        Message msg = catalog.locateMessage(null, "Hello Catalog");
        Assert.assertNotNull(msg);
        Assert.assertEquals("Hello Catalog", msg.getMsgid());
    }

}
