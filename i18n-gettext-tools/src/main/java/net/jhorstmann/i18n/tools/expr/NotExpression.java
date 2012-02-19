package net.jhorstmann.i18n.tools.expr;

import org.objectweb.asm.commons.GeneratorAdapter;

public class NotExpression extends Expression {

    private Expression child;

    public NotExpression(Expression child) {
        this.child = child;
    }

    @Override
    public long eval(long n) {
        return child.eval(n) == 0 ? 1 : 0;
    }

    @Override
    public boolean isBool() {
        return true;
    }

    @Override
    public void validate() throws InvalidExpressionException {
        if (!child.isBool()) {
            throw new InvalidExpressionException("Operator '!' can only be applied to boolean expressions");
        }
    }

    @Override
    public String toString() {
        return "!(" + child + ")";
    }

    @Override
    public void compile(GeneratorAdapter ga, int narg) {
        ga.not();
    }

    @Override
    public int computeStackSize() {
        return child.computeStackSize();
    }
}
