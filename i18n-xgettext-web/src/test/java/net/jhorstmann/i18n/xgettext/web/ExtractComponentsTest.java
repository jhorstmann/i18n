package net.jhorstmann.i18n.xgettext.web;

import org.fedorahosted.tennera.jgettext.Message;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.util.Collection;
import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractorException;

public class ExtractComponentsTest {
    private MessageBundle parseComponents() throws IOException, MessageExtractorException {
        MessageBundle bundle = new MessageBundle();
        InputSource input = new InputSource(getClass().getResourceAsStream("test_components.xhtml"));
        input.setSystemId("test_components.xhtml");
        new WebMessageExtractor(bundle).extractMessages(input);
        Assert.assertFalse(bundle.isEmpty());
        Assert.assertEquals(3, bundle.size());
        return bundle;
    }

    @Test
    public void componentMessage() throws IOException, MessageExtractorException {
        MessageBundle bundle = parseComponents();
        Message msg = bundle.getMessage("Hello World");
        Assert.assertNotNull(msg);
        Assert.assertEquals(1, msg.getSourceReferences().size());
        Assert.assertTrue(msg.getSourceReferences().get(0).endsWith("test_components.xhtml:7"));
        Assert.assertEquals("Hello World", msg.getMsgid());
        Assert.assertNull(msg.getMsgctxt());
    }

    @Test
    public void componentMessageWithContext() throws IOException, MessageExtractorException {
        MessageBundle bundle = parseComponents();
        Message msgWithContext = bundle.getMessage("Hello World (title)", "Hello World");
        Assert.assertNotNull(msgWithContext);
        Assert.assertEquals(1, msgWithContext.getSourceReferences().size());
        Assert.assertTrue(msgWithContext.getSourceReferences().get(0).endsWith("test_components.xhtml:4"));
        Assert.assertEquals("Hello World", msgWithContext.getMsgid());
        Assert.assertEquals("Hello World (title)", msgWithContext.getMsgctxt());
    }

    @Test
    public void componentMessageWithContextAndPlural() throws IOException, MessageExtractorException {
        MessageBundle bundle = parseComponents();
        Message msgWithContextAndPlural = bundle.getMessage("Hello World (plural)", "Hello World");
        Assert.assertNotNull(msgWithContextAndPlural);
        Assert.assertEquals(1, msgWithContextAndPlural.getSourceReferences().size());
        // Location of closing tag
        Assert.assertTrue(msgWithContextAndPlural.getSourceReferences().get(0).matches(".*test_components\\.xhtml:\\d+$"));
        System.out.println(msgWithContextAndPlural.getSourceReferences());
        Assert.assertEquals("Hello World (plural)", msgWithContextAndPlural.getMsgctxt());
        Assert.assertEquals("Hello World", msgWithContextAndPlural.getMsgid());
        Assert.assertEquals("Hello Worlds", msgWithContextAndPlural.getMsgidPlural());
        /*
        List<String> plurals = msgWithContextAndPlural.getMsgstrPlural();
        Assert.assertNotNull(plurals);
        Assert.assertEquals(2, plurals.size());
        Assert.assertEquals("Hello World", plurals.get(0));
        Assert.assertEquals("Hello Worlds", plurals.get(1));
        */
        Collection<String> comments = msgWithContextAndPlural.getExtractedComments();
        Assert.assertNotNull(comments);
        Assert.assertEquals(1, comments.size());
        Assert.assertEquals("This is a comment", comments.iterator().next());
    }
}
