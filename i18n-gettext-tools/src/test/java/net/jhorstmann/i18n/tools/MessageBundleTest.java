package net.jhorstmann.i18n.tools;

import java.io.File;
import java.io.IOException;
import org.fedorahosted.tennera.jgettext.Message;
import org.junit.Assert;
import org.junit.Test;

public class MessageBundleTest {

    @Test
    public void testHeaderMessageInProperties() throws IOException {
        MessageBundle bundle = MessageBundle.loadProperties(new File("src/test/resources/net/jhorstmann/i18n/tools/Header.properties"), "net.jhorstmann.i18n.tools.Header", null);
        Message header = bundle.getHeaderMessage();
        Assert.assertNotNull(header);
        Assert.assertEquals("", header.getMsgid());
        Assert.assertNotNull(header.getMsgstr());
        String pluralForms = bundle.getPluralForms();
        Assert.assertNotNull(pluralForms);

    }
}
