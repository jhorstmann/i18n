package net.jhorstmann.i18n.tools.expr;

import org.objectweb.asm.commons.GeneratorAdapter;

public class OrExpression extends BinaryExpression {

    public OrExpression(Expression leftExpr, Expression rightExpr) {
        super(leftExpr, rightExpr);
    }

    @Override
    public long eval(long n) {
        return (leftExpr.eval(n) != 0 || rightExpr.eval(n) != 0) ? 1 : 0;
    }

    @Override
    public boolean isBool() {
        return true;
    }

    @Override
    public void validate() throws InvalidExpressionException {
        if (!leftExpr.isBool()) {
            throw new InvalidExpressionException("Left child expression must be of boolean type");
        }
        if (!rightExpr.isBool()) {
            throw new InvalidExpressionException("Right child expression must be of boolean type");
        }
    }

    @Override
    public String toString() {
        return toString("||");
    }

    @Override
    public void compile(GeneratorAdapter ga, int narg) {
        compileBinaryInstruction(ga, narg, GeneratorAdapter.OR);
    }
}
