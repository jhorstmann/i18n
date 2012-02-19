package net.jhorstmann.i18n.tools;

import net.jhorstmann.i18n.tools.expr.Expression;

public class PluralForms {
    private final int numPlurals;
    private final Expression expression;

    public PluralForms(int numPlurals, Expression expression) {
        this.numPlurals = numPlurals;
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public int getNumPlurals() {
        return numPlurals;
    }
}
