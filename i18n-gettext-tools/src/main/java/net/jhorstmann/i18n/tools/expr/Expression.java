package net.jhorstmann.i18n.tools.expr;

import org.objectweb.asm.commons.GeneratorAdapter;

public abstract class Expression {

    public abstract long eval(long n);
    public abstract boolean isBool();
    public abstract void validate() throws InvalidExpressionException;
    @Override
    public abstract String toString();
    public abstract void compile(GeneratorAdapter ga, int narg);
    public abstract int computeStackSize();
}
