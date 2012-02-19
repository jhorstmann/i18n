package net.jhorstmann.i18n.tools.expr;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public abstract class ComparisonExpression extends BinaryExpression {

    public ComparisonExpression(Expression leftExpr, Expression rightExpr) {
        super(leftExpr, rightExpr);
    }

    public long evalCompare(long n) {
        long n1 = leftExpr.eval(n);
        long n2 = rightExpr.eval(n);
        return n1 < n2 ? -1 : (n1 == n2 ? 0 : 1);
    }

    @Override
    public boolean isBool() {
        return true;
    }

    @Override
    public void validate() throws InvalidExpressionException {
    }

    protected void compileComparison(GeneratorAdapter ga, int narg, int op) {
        Label thenLabel = new Label();
        Label retLabel = new Label();
        leftExpr.compile(ga, narg);
        rightExpr.compile(ga, narg);
        ga.ifCmp(Type.LONG_TYPE, op, thenLabel);
        ga.push(0L);
        ga.goTo(retLabel);
        ga.mark(thenLabel);
        ga.push(1L);
        ga.mark(retLabel);
    }
}
