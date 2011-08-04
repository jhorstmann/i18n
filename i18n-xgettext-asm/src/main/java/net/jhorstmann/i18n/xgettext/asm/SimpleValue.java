package net.jhorstmann.i18n.xgettext.asm;

import org.objectweb.asm.Type;

final class SimpleValue extends AbstractValue {

    public SimpleValue(Type type) {
        super(type);
    }

    @Override
    public int hashCode() {
        return typeHash();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof SimpleValue) {
            SimpleValue value = (SimpleValue) obj;
            return typeEquals(value);
        } else {
            return false;
        }
    }
}
