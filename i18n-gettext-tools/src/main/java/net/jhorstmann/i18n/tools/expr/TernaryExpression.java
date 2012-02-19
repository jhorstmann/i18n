package net.jhorstmann.i18n.tools.expr;

import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public class TernaryExpression extends Expression {

    private Expression ifExpr;
    private Expression thenExpr;
    private Expression elseExpr;

    public TernaryExpression(Expression ifExpr, Expression thenExpr, Expression elseExpr) {
        this.ifExpr = ifExpr;
        this.thenExpr = thenExpr;
        this.elseExpr = elseExpr;
    }

    @Override
    public long eval(long n) {
        long cond = ifExpr.eval(n);
        return cond != 0 ? thenExpr.eval(n) : elseExpr.eval(n);
    }

    @Override
    public boolean isBool() {
        return thenExpr.isBool() && elseExpr.isBool();
    }

    @Override
    public void validate() throws InvalidExpressionException {
        if (!ifExpr.isBool()) {
            throw new InvalidExpressionException("Condition must be of boolean type");
        }
    }

    @Override
    public String toString() {
        return "(" + ifExpr + ") ? (" + thenExpr + ") : (" + elseExpr + ")";
    }

    @Override
    public void compile(GeneratorAdapter ga, int narg) {
        Label thenLabel = new Label();
        Label retLabel = new Label();
        ifExpr.compile(ga, narg);
        ga.push(0L);
        ga.ifCmp(Type.LONG_TYPE, GeneratorAdapter.NE, thenLabel);
        elseExpr.compile(ga, narg);
        ga.goTo(retLabel);
        ga.mark(thenLabel);
        thenExpr.compile(ga, narg);
        ga.mark(retLabel);
    }

    @Override
    public int computeStackSize() {
        return Math.max(2+ifExpr.computeStackSize(), Math.max(thenExpr.computeStackSize(), elseExpr.computeStackSize()));
    }
}
