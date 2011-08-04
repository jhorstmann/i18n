package net.jhorstmann.i18n.xgettext.asm;

import org.objectweb.asm.Type;

final class ConstantNullValue extends AbstractValue {
    private static final Type TYPE_NULL = Type.getObjectType("java/lang/Object");

    ConstantNullValue() {
        super(TYPE_NULL);
    }

    @Override
    public int hashCode() {
        return 7;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof ConstantNullValue;
    }
}
