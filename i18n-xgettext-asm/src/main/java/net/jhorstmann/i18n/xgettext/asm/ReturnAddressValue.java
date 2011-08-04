package net.jhorstmann.i18n.xgettext.asm;

import org.objectweb.asm.Type;

final class ReturnAddressValue extends AbstractValue {

    ReturnAddressValue() {
        super(Type.VOID_TYPE);
    }

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ReturnAddressValue;
    }
}
