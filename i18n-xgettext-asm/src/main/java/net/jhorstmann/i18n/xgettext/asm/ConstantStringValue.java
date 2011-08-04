package net.jhorstmann.i18n.xgettext.asm;

import org.objectweb.asm.Type;

final class ConstantStringValue extends AbstractValue {
    private static final Type STRING_TYPE = Type.getObjectType("java/lang/String");
    private String constantValue;

    ConstantStringValue(String constantValue) {
        super(STRING_TYPE);
        this.constantValue = constantValue;
    }

    public String getConstantValue() {
        return constantValue;
    }

    @Override
    public int hashCode() {
        return typeHash();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ConstantStringValue) {
            ConstantStringValue value = (ConstantStringValue) obj;
            return typeEquals(value) && (constantValue == null ? value.constantValue == null : constantValue.equals(value.constantValue));
        } else {
            return false;
        }
    }
    
}
