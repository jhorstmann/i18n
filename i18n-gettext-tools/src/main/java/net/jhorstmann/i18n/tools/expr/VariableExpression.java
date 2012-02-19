package net.jhorstmann.i18n.tools.expr;

import org.objectweb.asm.commons.GeneratorAdapter;

public class VariableExpression extends Expression {

    @Override
    public long eval(long n) {
        return n;
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
        return "n";
    }

    @Override
    public void compile(GeneratorAdapter ga, int narg) {
        ga.loadArg(narg);
    }

    @Override
    public int computeStackSize() {
        return 2;
    }

}
