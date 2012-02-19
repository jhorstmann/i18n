package net.jhorstmann.i18n.tools.expr;

import org.objectweb.asm.commons.GeneratorAdapter;

public class ConstantExpression extends Expression {
    private long value;

    public ConstantExpression(long value) {
        this.value = value;
    }

    @Override
    public long eval(long n) {
        return value;
    }

    @Override
    public boolean isBool() {
        return value == 0 || value == 1;
    }

    @Override
    public void validate() throws InvalidExpressionException {
    }

    @Override
    public String toString() {
        return Long.toString(value);
    }

    @Override
    public void compile(GeneratorAdapter ga, int narg) {
        ga.push(value);
    }

    @Override
    public int computeStackSize() {
        return 2;
    }

}
