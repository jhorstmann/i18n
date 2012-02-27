package net.jhorstmann.i18n.tools;

import org.fedorahosted.tennera.jgettext.Message;
import org.junit.Assert;
import org.junit.Test;

public class MessageUtilsTest {

    @Test
    public void testNoContext() {
        Assert.assertEquals("id", MessageUtils.serializeMsgid(null, "id"));
    }

    @Test
    public void testContext() {
        Assert.assertEquals("ctx\u0004id", MessageUtils.serializeMsgid("ctx", "id"));
    }

    @Test
    public void testContextAndPlural() {
        Message msg = new Message();
        msg.setMsgctxt("ctx");
        msg.setMsgid("id");
        msg.setMsgidPlural("ids");
        Assert.assertEquals("ctx\u0004id\u0000ids", MessageUtils.serializeMsgid(msg));
    }

    @Test
    public void testString() {
        Message msg = new Message();
        msg.setMsgid("id");
        msg.setMsgstr("str");
        Assert.assertEquals("str", MessageUtils.serializeMsgstr(msg));
    }

    @Test
    public void testStringPlural() {
        Message msg = new Message();
        msg.setMsgid("id");
        msg.setMsgidPlural("ids");
        msg.addMsgstrPlural("id", 0);
        msg.addMsgstrPlural("ids", 1);
        Assert.assertEquals("id\u0000ids", MessageUtils.serializeMsgstr(msg));
    }
}
