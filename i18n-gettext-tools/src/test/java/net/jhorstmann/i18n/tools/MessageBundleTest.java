package net.jhorstmann.i18n.tools;

import java.io.File;
import java.io.IOException;

import org.fedorahosted.tennera.jgettext.HeaderFields;
import org.fedorahosted.tennera.jgettext.HeaderUtil;
import org.fedorahosted.tennera.jgettext.Message;
import org.junit.Assert;
import org.junit.Test;

public class MessageBundleTest {

    @Test
    public void testHeaderMessageInProperties() throws IOException {
        MessageBundle bundle = MessageBundle.loadProperties(new File("src/test/resources/net/jhorstmann/i18n/tools/Header.properties"),
                "net.jhorstmann.i18n.tools.Header", null);
        Message header = bundle.getHeaderMessage();
        Assert.assertNotNull(header);
        Assert.assertEquals("", header.getMsgid());
        Assert.assertNotNull(header.getMsgstr());
        String pluralForms = bundle.getPluralForms();
        Assert.assertNotNull(pluralForms);

    }

    @Test
    public void testBundleWithHeaderMessage() {
        MessageBundle bundle = new MessageBundle();
        Message header = HeaderUtil.generateDefaultHeader();
        bundle.addMessage(header);

        Message tstHeader = bundle.getHeaderMessage();
        Assert.assertNotNull(tstHeader);
    }

    @Test
    public void testBundleWithHeaderFields() {
        MessageBundle bundle = new MessageBundle();
        Message header = HeaderUtil.generateDefaultHeader();
        HeaderFields fields = HeaderFields.wrap(header);

        fields.setValue(HeaderFields.KEY_ReportMsgidBugsTo, "a@b.com");

        fields.unwrap(header);
        bundle.addMessage(header);

        Message tstHeader = bundle.getHeaderMessage();
        Assert.assertNotNull(tstHeader);
        fields = HeaderFields.wrap(header);
        Assert.assertTrue("a@b.com".equals(fields.getValue(HeaderFields.KEY_ReportMsgidBugsTo)));
    }

}
