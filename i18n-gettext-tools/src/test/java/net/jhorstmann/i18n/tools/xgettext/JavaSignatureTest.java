package net.jhorstmann.i18n.tools.xgettext;

import org.junit.Assert;
import org.junit.Test;

public class JavaSignatureTest {

    private static final String CLS = "net.jhorstmann.i18n";
    private static final String FN = "tr";
    private static final String NS = "net/jhorstmann/i18n";
    
    @Test
    public void testNoArgs() {
        MessageFunction fn = MessageFunction.fromJava(CLS, "tr()");

        Assert.assertEquals(NS, fn.getNamespace());
        Assert.assertEquals(FN, fn.getName());
        Assert.assertEquals("()V", fn.getDescription());
        Assert.assertEquals(-1, fn.getContextIndex());
        Assert.assertEquals(-1, fn.getMessageIndex());
        Assert.assertEquals(-1, fn.getPluralIndex());
    }
    
    @Test
    public void testStringArg() {
        MessageFunction fn = MessageFunction.fromJava(CLS, "tr(java.lang.String message)");

        Assert.assertEquals(NS, fn.getNamespace());
        Assert.assertEquals(FN, fn.getName());
        Assert.assertEquals("(Ljava/lang/String;)V", fn.getDescription());
        Assert.assertEquals(0, fn.getMessageIndex());
    }

    @Test
    public void testStringArrayArg() {
        MessageFunction fn = MessageFunction.fromJava(CLS, "tr(java.lang.String[][] test)");

        Assert.assertEquals(NS, fn.getNamespace());
        Assert.assertEquals(FN, fn.getName());
        Assert.assertEquals("([[Ljava/lang/String;)V", fn.getDescription());
        Assert.assertEquals(-1, fn.getMessageIndex());
    }

    @Test
    public void testStringArgs() {
        MessageFunction fn = MessageFunction.fromJava(CLS, "tr(java.lang.String context, java.lang.String message)");

        Assert.assertEquals(NS, fn.getNamespace());
        Assert.assertEquals(FN, fn.getName());
        Assert.assertEquals("(Ljava/lang/String;Ljava/lang/String;)V", fn.getDescription());
        Assert.assertEquals(0, fn.getContextIndex());
        Assert.assertEquals(1, fn.getMessageIndex());
    }

    @Test
    public void testVarArgs() {
        MessageFunction fn = MessageFunction.fromJava(CLS, "tr(java.lang.String message, java.lang.String... params)");

        Assert.assertEquals(NS, fn.getNamespace());
        Assert.assertEquals(FN, fn.getName());
        Assert.assertEquals("(Ljava/lang/String;[Ljava/lang/String;)V", fn.getDescription());
        Assert.assertEquals(0, fn.getMessageIndex());
    }

    @Test
    public void testReturnType() {
        MessageFunction fn = MessageFunction.fromJava(CLS, "String tr(java.lang.String message, java.lang.String... params)");

        Assert.assertEquals(NS, fn.getNamespace());
        Assert.assertEquals(FN, fn.getName());
        Assert.assertEquals("(Ljava/lang/String;[Ljava/lang/String;)Ljava/lang/String;", fn.getDescription());
        Assert.assertEquals(0, fn.getMessageIndex());
    }

    @Test
    public void testVoidReturnType() {
        MessageFunction fn = MessageFunction.fromJava(CLS, "void tr(java.lang.String message, java.lang.String... params)");

        Assert.assertEquals(NS, fn.getNamespace());
        Assert.assertEquals(FN, fn.getName());
        Assert.assertEquals("(Ljava/lang/String;[Ljava/lang/String;)V", fn.getDescription());
        Assert.assertEquals(0, fn.getMessageIndex());
    }
    
}
