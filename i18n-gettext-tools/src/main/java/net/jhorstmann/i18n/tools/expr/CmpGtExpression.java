package net.jhorstmann.i18n.tools.expr;

import org.objectweb.asm.commons.GeneratorAdapter;

public class CmpGtExpression extends ComparisonExpression {

    public CmpGtExpression(Expression leftExpr, Expression rightExpr) {
        super(leftExpr, rightExpr);
    }

    @Override
    public long eval(long n) {
        return evalCompare(n) > 0 ? 1 : 0;
    }

    @Override
    public String toString() {
        return toString(">");
    }

    @Override
    public void compile(GeneratorAdapter ga, int narg) {
        compileComparison(ga, narg, GeneratorAdapter.GT);
    }
}
