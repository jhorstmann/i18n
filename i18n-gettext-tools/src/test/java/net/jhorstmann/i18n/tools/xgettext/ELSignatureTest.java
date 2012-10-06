package net.jhorstmann.i18n.tools.xgettext;

import junit.framework.Assert;
import org.junit.Test;

public class ELSignatureTest {

    private static final String NS = "http://net.jhorstmann/i18n/taglib";

    @Test
    public void testNoArg() {
        MessageFunction fn = MessageFunction.fromEL(NS, "tr()");
        Assert.assertEquals("tr", fn.getName());
        Assert.assertEquals(NS, fn.getNamespace());
        Assert.assertEquals(-1, fn.getMessageIndex());
        Assert.assertEquals(-1, fn.getContextIndex());
        Assert.assertEquals(-1, fn.getPluralIndex());
    }

    @Test
    public void testInlineNamespace() {
        MessageFunction fn = MessageFunction.fromEL("{" + NS + "}tr()");
        Assert.assertEquals("tr", fn.getName());
        Assert.assertEquals(NS, fn.getNamespace());
        Assert.assertEquals(-1, fn.getMessageIndex());
        Assert.assertEquals(-1, fn.getContextIndex());
        Assert.assertEquals(-1, fn.getPluralIndex());
    }

    @Test
    public void testMsg() {
        MessageFunction fn = MessageFunction.fromEL(NS, "tr(message)");
        Assert.assertEquals("tr", fn.getName());
        Assert.assertEquals(NS, fn.getNamespace());
        Assert.assertEquals(0, fn.getMessageIndex());
        Assert.assertEquals(-1, fn.getContextIndex());
        Assert.assertEquals(-1, fn.getPluralIndex());
    }

    @Test
    public void testContext() {
        MessageFunction fn = MessageFunction.fromEL(NS, "tr(context, message)");
        Assert.assertEquals("tr", fn.getName());
        Assert.assertEquals(NS, fn.getNamespace());
        Assert.assertEquals(0, fn.getContextIndex());
        Assert.assertEquals(1, fn.getMessageIndex());
        Assert.assertEquals(-1, fn.getPluralIndex());
    }
}
