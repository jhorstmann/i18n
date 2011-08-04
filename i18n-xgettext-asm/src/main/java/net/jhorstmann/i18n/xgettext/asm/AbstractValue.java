package net.jhorstmann.i18n.xgettext.asm;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.analysis.Value;

abstract class AbstractValue implements Value {
    private Type type;

    AbstractValue(Type type) {
        this.type = type;
    }

    final Type getType() {
        return type;
    }

    @Override
    public final int getSize() {
        return type == Type.LONG_TYPE || type == Type.DOUBLE_TYPE ? 2 : 1;
    }

    final int typeHash() {
        return type == null ? 0 : type.hashCode();
    }

    final boolean typeEquals(AbstractValue other) {
        return type == null ? other.type == null : type.equals(other.type);
    }
}
