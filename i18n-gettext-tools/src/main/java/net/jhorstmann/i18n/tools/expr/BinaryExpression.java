package net.jhorstmann.i18n.tools.expr;

import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;

public abstract class BinaryExpression extends Expression {
    protected final Expression leftExpr;
    protected final Expression rightExpr;

    public BinaryExpression(Expression leftExpr, Expression rightExpr) {
        this.leftExpr = leftExpr;
        this.rightExpr = rightExpr;
    }

    protected String toString(String op) {
        return "(" + leftExpr + ") " + op + " (" + rightExpr + ")";
    }

    protected void compileBinaryInstruction(GeneratorAdapter ga, int narg, int insn) {
        leftExpr.compile(ga, narg);
        rightExpr.compile(ga, narg);
        ga.math(insn, Type.LONG_TYPE);
    }

    @Override
    public int computeStackSize() {
        return Math.max(leftExpr.computeStackSize(), 2+rightExpr.computeStackSize());
    }
}
