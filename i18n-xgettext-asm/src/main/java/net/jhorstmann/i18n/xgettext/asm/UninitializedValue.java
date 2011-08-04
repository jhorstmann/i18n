package net.jhorstmann.i18n.xgettext.asm;

final class UninitializedValue extends AbstractValue {

    UninitializedValue() {
        super(null);
    }

    @Override
    public int hashCode() {
        return 3;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof UninitializedValue;
    }
    
}
