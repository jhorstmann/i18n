package net.jhorstmann.i18n.xgettext.web;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractorException;

import org.fedorahosted.tennera.jgettext.Message;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class ExtractFunctionsTest {

    private MessageBundle parseFunctions() throws IOException, MessageExtractorException {
        MessageBundle bundle = new MessageBundle();
        InputSource input = new InputSource(getClass().getResourceAsStream("test_functions.xhtml"));
        input.setSystemId("test_functions.xhtml");
        new WebMessageExtractor(bundle).extractMessages(input);
        Assert.assertFalse(bundle.isEmpty());
        Assert.assertEquals(5, bundle.size());
        return bundle;
    }

    @Test
    public void functionMessage() throws IOException, MessageExtractorException {
        MessageBundle bundle = parseFunctions();
        Message msg = bundle.getMessage("Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals(1, msg.getSourceReferences().size());
        Assert.assertEquals("Hello World", msg.getMsgid());
        Assert.assertNull(msg.getMsgctxt());
    }

    @Test
    public void functionMessageWithParameters() throws IOException, MessageExtractorException {
        MessageBundle bundle = parseFunctions();
        Message msg = bundle.getMessage("This is a test with parameters {0}, {1} and {2}");
        Assert.assertNotNull(msg);
        Assert.assertEquals(1, msg.getSourceReferences().size());
        Assert.assertEquals("This is a test with parameters {0}, {1} and {2}", msg.getMsgid());
        Assert.assertNull(msg.getMsgctxt());
    }

    @Test
    public void functionMessageWithContext() throws IOException, MessageExtractorException {
        MessageBundle bundle = parseFunctions();
        Message msgWithContext = bundle.getMessage("Hello World (title)", "Hello World");
        Assert.assertNotNull(msgWithContext);
        Assert.assertEquals(1, msgWithContext.getSourceReferences().size());
        Assert.assertTrue(msgWithContext.getSourceReferences().get(0).endsWith("test_functions.xhtml:5"));
        Assert.assertEquals("Hello World", msgWithContext.getMsgid());
        Assert.assertEquals("Hello World (title)", msgWithContext.getMsgctxt());
    }

    @Test
    public void functionMessageWithPlural() throws IOException, MessageExtractorException {
        MessageBundle bundle = parseFunctions();
        Message msgWithPlural = bundle.getMessage("This is a test");
        Assert.assertNotNull(msgWithPlural);
        Assert.assertEquals(1, msgWithPlural.getSourceReferences().size());
        Assert.assertEquals("This is a test", msgWithPlural.getMsgid());
        Assert.assertEquals("These are {0} tests", msgWithPlural.getMsgidPlural());
    }

    @Test
    public void functionMessageWithContextAndPlural() throws ParserConfigurationException, SAXException, IOException, MessageExtractorException {
        MessageBundle bundle = parseFunctions();
        Message msgWithContextAndPlural = bundle.getMessage("This is a test (with context)", "This is a test");
        Assert.assertNotNull(msgWithContextAndPlural);
        Assert.assertEquals(1, msgWithContextAndPlural.getSourceReferences().size());
        Assert.assertEquals("This is a test", msgWithContextAndPlural.getMsgid());
        Assert.assertEquals("This is a test (with context)", msgWithContextAndPlural.getMsgctxt());
        Assert.assertEquals("These are {0} tests", msgWithContextAndPlural.getMsgidPlural());
    }
}
