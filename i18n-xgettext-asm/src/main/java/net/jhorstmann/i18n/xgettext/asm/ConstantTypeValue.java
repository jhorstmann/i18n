package net.jhorstmann.i18n.xgettext.asm;

import org.objectweb.asm.Type;

final class ConstantTypeValue extends AbstractValue {
    private static final Type CLASS_TYPE = Type.getObjectType("java/lang/Class");
    private Type typeValue;

    ConstantTypeValue(Type typeValue) {
        super(CLASS_TYPE);
        this.typeValue = typeValue;
    }

    public Type getConstantType() {
        return typeValue;
    }

    @Override
    public int hashCode() {
        return typeHash();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ConstantTypeValue) {
            ConstantTypeValue value = (ConstantTypeValue) obj;
            return typeEquals(value) && (typeValue == null ? value.typeValue == null : typeValue.equals(value.typeValue));
        } else {
            return false;
        }
    }
}
