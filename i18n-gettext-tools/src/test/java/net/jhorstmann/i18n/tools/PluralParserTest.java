package net.jhorstmann.i18n.tools;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import junit.framework.Assert;
import net.jhorstmann.i18n.tools.expr.ConstantExpression;
import net.jhorstmann.i18n.tools.expr.Expression;
import net.jhorstmann.i18n.tools.expr.NotExpression;
import net.jhorstmann.i18n.tools.expr.VariableExpression;
import net.jhorstmann.i18n.tools.expr.OrExpression;
import org.junit.Test;
import static net.jhorstmann.i18n.tools.PluralsParser.parseExpression;
import static net.jhorstmann.i18n.tools.PluralsParser.parsePluralForms;
import net.jhorstmann.i18n.tools.expr.TernaryExpression;

public class PluralParserTest {

    @Test
    public void testPluralForms() throws RecognitionException, TokenStreamException {
        PluralForms pf = parsePluralForms("nplurals=2; plural=n == 1 ? 0 : 1;");
        Assert.assertNotNull(pf);
        Assert.assertEquals(2, pf.getNumPlurals());
        Expression expr = pf.getExpression();
        Assert.assertNotNull(expr);
        Assert.assertTrue(expr.isBool());
        Assert.assertEquals(1, expr.eval(0));
        Assert.assertEquals(0, expr.eval(1));
        Assert.assertEquals(1, expr.eval(2));
        Assert.assertEquals("((n) == (1)) ? (0) : (1)", expr.toString());
    }

    @Test
    public void testNumExpression() throws RecognitionException, TokenStreamException {
        Expression e = parseExpression("n");
        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof VariableExpression);
        Assert.assertFalse(e.isBool());
        Assert.assertEquals(0, e.eval(0));
        Assert.assertEquals(1, e.eval(1));
        Assert.assertEquals(2, e.eval(2));
        Assert.assertEquals("n", e.toString());
    }

    @Test
    public void testConstantExpression() throws RecognitionException, TokenStreamException {
        Expression e = parseExpression("123");
        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof ConstantExpression);
        Assert.assertFalse(e.isBool());
        Assert.assertEquals(123, e.eval(0));
        Assert.assertEquals(123, e.eval(1));
        Assert.assertEquals(123, e.eval(2));
        Assert.assertEquals("123", e.toString());
    }

    @Test
    public void testNotExpression() throws RecognitionException, TokenStreamException {
        Expression e = parseExpression("!0");
        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof NotExpression);
        Assert.assertTrue(e.isBool());
        Assert.assertEquals(1, e.eval(0));
        Assert.assertEquals(1, e.eval(1));
        Assert.assertEquals(1, e.eval(2));
        Assert.assertEquals("!(0)", e.toString());
    }

    @Test
    public void testNotExpression2() throws RecognitionException, TokenStreamException {
        Expression e = parseExpression("!(0)");
        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof NotExpression);
        Assert.assertTrue(e.isBool());
        Assert.assertEquals(1, e.eval(0));
        Assert.assertEquals(1, e.eval(1));
        Assert.assertEquals(1, e.eval(2));
        Assert.assertEquals("!(0)", e.toString());
    }

    @Test
    public void testNotExpression3() throws RecognitionException, TokenStreamException {
        Expression e = parseExpression("!(n==1)");
        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof NotExpression);
        Assert.assertTrue(e.isBool());
        Assert.assertEquals(1, e.eval(0));
        Assert.assertEquals(0, e.eval(1));
        Assert.assertEquals(1, e.eval(2));
        Assert.assertEquals("!((n) == (1))", e.toString());
    }

    @Test
    public void testOrExpression() throws RecognitionException, TokenStreamException {
        Expression e = parseExpression("n==0 || n==1");
        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof OrExpression);
        Assert.assertTrue(e.isBool());
        Assert.assertEquals(1, e.eval(0));
        Assert.assertEquals(1, e.eval(1));
        Assert.assertEquals(0, e.eval(2));
        Assert.assertEquals("((n) == (0)) || ((n) == (1))", e.toString());
    }

    @Test
    public void testOrExpression2() throws RecognitionException, TokenStreamException {
        Expression e = parseExpression("n==0 || n==1 || n==2");
        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof OrExpression);
        Assert.assertTrue(e.isBool());
        System.out.println(e.toString());
        Assert.assertEquals(1, e.eval(0));
        Assert.assertEquals(1, e.eval(1));
        Assert.assertEquals(1, e.eval(2));
        Assert.assertEquals(0, e.eval(3));
        Assert.assertEquals("(((n) == (0)) || ((n) == (1))) || ((n) == (2))", e.toString());
    }

    @Test
    public void testTernaryExpression() throws RecognitionException, TokenStreamException {
        Expression e = parseExpression("n==0 ? 0 : n==1 ? 1 : 0");
        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof TernaryExpression);
        Assert.assertTrue(e.isBool());
        System.out.println(e.toString());
        Assert.assertEquals(0, e.eval(0));
        Assert.assertEquals(1, e.eval(1));
        Assert.assertEquals(0, e.eval(2));
        Assert.assertEquals(0, e.eval(3));
        Assert.assertEquals("((n) == (0)) ? (0) : (((n) == (1)) ? (1) : (0))", e.toString());
    }

    @Test
    public void testTernaryExpression2() throws RecognitionException, TokenStreamException {
        Expression e = parseExpression("n>0 ? n==1 ? 1 : 2 : 0");
        Assert.assertNotNull(e);
        Assert.assertTrue(e instanceof TernaryExpression);
        Assert.assertFalse(e.isBool());
        System.out.println(e.toString());
        Assert.assertEquals(0, e.eval(0));
        Assert.assertEquals(1, e.eval(1));
        Assert.assertEquals(2, e.eval(2));
        Assert.assertEquals(2, e.eval(3));
        Assert.assertEquals("((n) > (0)) ? (((n) == (1)) ? (1) : (2)) : (0)", e.toString());
    }

}
