package net.jhorstmann.i18n.tools;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import java.io.StringReader;
import junit.framework.Assert;
import net.jhorstmann.i18n.tools.expr.Expression;
import net.jhorstmann.i18n.tools.expr.InvalidExpressionException;
import net.jhorstmann.i18n.tools.expr.NotExpression;
import net.jhorstmann.i18n.tools.expr.OrExpression;
import org.junit.Test;

public class ExprParserTest {
    private Expression parseExpression(String str) throws RecognitionException, TokenStreamException {
        ExprLexer lexer = new ExprLexer(new StringReader(str));
        ExprParser parser = new ExprParser(lexer);
        return parser.expression();
    }

    @Test(expected=RecognitionException.class)
    public void testInvalid() throws RecognitionException, TokenStreamException, InvalidExpressionException {
        Expression e = parseExpression("0 || ");
        System.out.println(e);
        e.validate();
    }

    @Test
    public void testOrExpression() throws RecognitionException, TokenStreamException, InvalidExpressionException {
        Expression e = parseExpression("0 || 1 || 1");
        Assert.assertNotNull(e);
        System.out.println(e);
        e.validate();
        Assert.assertTrue(e instanceof OrExpression);
        Assert.assertTrue(e.isBool());
        Assert.assertEquals(1, e.eval(0));
        Assert.assertEquals(1, e.eval(1));
        Assert.assertEquals(1, e.eval(2));
        Assert.assertEquals("((0) || (1)) || (1)", e.toString());
    }

    @Test
    public void testNotExpression() throws RecognitionException, TokenStreamException, InvalidExpressionException {
        Expression e = parseExpression("!(0)");
        Assert.assertNotNull(e);
        System.out.println(e);
        e.validate();
        Assert.assertTrue(e instanceof NotExpression);
        Assert.assertTrue(e.isBool());
        Assert.assertEquals(1, e.eval(0));
        Assert.assertEquals(1, e.eval(1));
        Assert.assertEquals(1, e.eval(2));
        Assert.assertEquals("!(0)", e.toString());
    }

    @Test
    public void testOrExpression2() throws RecognitionException, TokenStreamException, InvalidExpressionException {
        Expression e = parseExpression("0 || (1 || 0)");
        Assert.assertNotNull(e);
        System.out.println(e);
        e.validate();
        Assert.assertTrue(e instanceof OrExpression);
        Assert.assertTrue(e.isBool());
        Assert.assertEquals(1, e.eval(0));
        Assert.assertEquals(1, e.eval(1));
        Assert.assertEquals(1, e.eval(2));
        Assert.assertEquals("(0) || ((1) || (0))", e.toString());
    }

    @Test
    public void testAndExpression() throws RecognitionException, TokenStreamException, InvalidExpressionException {
        Expression e = parseExpression("0 || 1 && 1");
        Assert.assertNotNull(e);
        System.out.println(e);
        e.validate();
        Assert.assertTrue(e instanceof OrExpression);
        Assert.assertTrue(e.isBool());
        Assert.assertEquals(1, e.eval(0));
        Assert.assertEquals(1, e.eval(1));
        Assert.assertEquals(1, e.eval(2));
        Assert.assertEquals("(0) || ((1) && (1))", e.toString());
    }

    @Test
    public void testAndExpression2() throws RecognitionException, TokenStreamException, InvalidExpressionException {
        Expression e = parseExpression("0 || 1 && 0 || 0");
        Assert.assertNotNull(e);
        System.out.println(e);
        e.validate();
        Assert.assertTrue(e instanceof OrExpression);
        Assert.assertTrue(e.isBool());
        Assert.assertEquals(0, e.eval(0));
        Assert.assertEquals(0, e.eval(1));
        Assert.assertEquals(0, e.eval(2));
        Assert.assertEquals("((0) || ((1) && (0))) || (0)", e.toString());
    }

}
