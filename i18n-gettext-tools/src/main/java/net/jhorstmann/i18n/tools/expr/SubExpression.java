package net.jhorstmann.i18n.tools.expr;

import org.objectweb.asm.commons.GeneratorAdapter;

public class SubExpression extends BinaryExpression {

    public SubExpression(Expression leftExpr, Expression rightExpr) {
        super(leftExpr, rightExpr);
    }

    @Override
    public long eval(long n) {
        return leftExpr.eval(n) - rightExpr.eval(n);
    }

    @Override
    public boolean isBool() {
        return false;
    }

    @Override
    public void validate() throws InvalidExpressionException {
    }

    @Override
    public String toString() {
        return toString("-");
    }

    @Override
    public void compile(GeneratorAdapter ga, int narg) {
        compileBinaryInstruction(ga, narg, GeneratorAdapter.SUB);
    }
}
