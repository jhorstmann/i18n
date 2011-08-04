package net.jhorstmann.i18n.xgettext.asm.poc;

import org.objectweb.asm.tree.MethodNode;
import java.util.Iterator;
import org.objectweb.asm.tree.analysis.Analyzer;
import java.io.IOException;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import java.io.InputStream;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

import static org.objectweb.asm.Opcodes.*;

public class ConstantTracker implements Interpreter {
    
    static final class UninitializedValue implements Value {

        @Override
        public int getSize() {
            return 1;
        }
        
    }

    static final class SimpleValue implements Value {

        @Override
        public int getSize() {
            return 1;
        }
    }

    /**
     * A value that uses 2 stack slots, long or double.
     */
    static final class LongValue implements Value {

        @Override
        public int getSize() {
            return 2;
        }
    }

    static final class ConstantValue implements Value {

        private String string;

        public ConstantValue(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }

        @Override
        public int getSize() {
            return 1;
        }
    }
    private static final Value UNINITIALIZED_VALUE = new UninitializedValue();
    private static final Value SIMPLE_VALUE = new SimpleValue();
    private static final Value LONG_VALUE = new LongValue();
    
    public static void findConstantArgumentsToPrintln(InputStream in) throws IOException, AnalyzerException {
        ClassReader classReader = new ClassReader(in);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        findConstantArgumentsToPrintln(classNode);
    }

    public static void findConstantArgumentsToPrintln(ClassNode classNode) throws AnalyzerException {
        Interpreter interpreter = new ConstantTracker();
        Analyzer analyzer = new Analyzer(interpreter);

        for (Iterator i = classNode.methods.iterator(); i.hasNext();) {
            MethodNode methodNode = (MethodNode)i.next();
            analyzer.analyze(classNode.name, methodNode);
        }
    }
    
    @Override
    public Value newValue(Type type) {
        if (type == null) {
            return UNINITIALIZED_VALUE;
        } else {
            switch (type.getSort()) {
                case Type.VOID:
                    return null;
                case Type.BOOLEAN:
                case Type.CHAR:
                case Type.BYTE:
                case Type.SHORT:
                case Type.INT:
                case Type.FLOAT:
                case Type.ARRAY:
                case Type.OBJECT:
                    return SIMPLE_VALUE;
                case Type.LONG:
                case Type.DOUBLE:
                    return LONG_VALUE;
                default:
                    throw new IllegalStateException("Unhandled type " + type);
            }
        }
    }

    @Override
    public Value newOperation(final AbstractInsnNode insn) throws AnalyzerException {
        int opcode = insn.getOpcode();
        switch (opcode) {
            case ACONST_NULL:
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
            case BIPUSH:
            case SIPUSH:
                return SIMPLE_VALUE;
            case LCONST_0:
            case LCONST_1:
            case DCONST_0:
            case DCONST_1:
                return LONG_VALUE;
            case LDC:
                Object cst = ((LdcInsnNode)insn).cst;
                if (cst instanceof String) {
                    return new ConstantValue((String)cst);
                } else {
                    return newValue(Type.getType(cst.getClass()));
                }
            case JSR:
                return SIMPLE_VALUE;
            case GETSTATIC:
                return newValue(Type.getType(((FieldInsnNode)insn).desc));
            case NEW:
                return newValue(Type.getObjectType(((TypeInsnNode)insn).desc));
            default:
                throw new IllegalStateException("Unhandled opcode " + opcode);
        }
    }

    @Override
    public Value copyOperation(AbstractInsnNode insn, Value value) throws AnalyzerException {
        return value;
    }

    @Override
    public Value unaryOperation(AbstractInsnNode insn, Value value) throws AnalyzerException {
        int opcode = insn.getOpcode();
        switch (opcode) {
            case INEG:
            case IINC:
            case L2I:
            case F2I:
            case D2I:
            case I2B:
            case I2C:
            case I2S:
            case FNEG:
            case I2F:
            case L2F:
            case D2F:
            case NEWARRAY:
            case ANEWARRAY:
            case ARRAYLENGTH:
            case INSTANCEOF:
                return SIMPLE_VALUE;
            case LNEG:
            case I2L:
            case F2L:
            case D2L:
            case DNEG:
            case I2D:
            case L2D:
            case F2D:
                return LONG_VALUE;
            case GETFIELD:
                return newValue(Type.getType(((FieldInsnNode)insn).desc));
            case CHECKCAST:
                return newValue(Type.getObjectType(((TypeInsnNode)insn).desc));
            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case TABLESWITCH:
            case LOOKUPSWITCH:
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case PUTSTATIC:
            case MONITORENTER:
            case MONITOREXIT:
            case IFNULL:
            case IFNONNULL:
            case ATHROW:
                return null;
            default:
                throw new IllegalStateException("Unhandled opcode " + opcode);
        }
    }

    @Override
    public Value binaryOperation(AbstractInsnNode insn, Value value1, Value value2) throws AnalyzerException {
        int opcode = insn.getOpcode();
        switch (opcode) {
            case IALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
            case IADD:
            case ISUB:
            case IMUL:
            case IDIV:
            case IREM:
            case ISHL:
            case ISHR:
            case IUSHR:
            case IAND:
            case IOR:
            case IXOR:
            case FALOAD:
            case FADD:
            case FSUB:
            case FMUL:
            case FDIV:
            case FREM:
            case AALOAD:
            case LCMP:
            case FCMPL:
            case FCMPG:
            case DCMPL:
            case DCMPG:
                return SIMPLE_VALUE;
            case LALOAD:
            case LADD:
            case LSUB:
            case LMUL:
            case LDIV:
            case LREM:
            case LSHL:
            case LSHR:
            case LUSHR:
            case LAND:
            case LOR:
            case LXOR:
            case DALOAD:
            case DADD:
            case DSUB:
            case DMUL:
            case DDIV:
            case DREM:
                return LONG_VALUE;
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case PUTFIELD:
                return null;
            default:
                throw new IllegalStateException("Unhandled opcode " + opcode);
        }
    }

    @Override
    public Value ternaryOperation(AbstractInsnNode insn, Value value1, Value value2, Value value3) throws AnalyzerException {
        return null;
    }

    @Override
    public Value naryOperation(AbstractInsnNode insn, List values) throws AnalyzerException {
        int opcode = insn.getOpcode();
        if (opcode == MULTIANEWARRAY) {
            return SIMPLE_VALUE;
        } else {
            MethodInsnNode methodInsn = (MethodInsnNode)insn;
            String owner = methodInsn.owner;
            String name = methodInsn.name;
            String desc = methodInsn.desc;

            if (opcode == INVOKEVIRTUAL && "java/io/PrintStream".equals(owner) && "println".equals(name) && "(Ljava/lang/String;)V".equals(desc)) {
                Value arg = (Value)values.get(1);
                if (arg instanceof ConstantValue) {
                    ConstantValue cons = (ConstantValue)arg;
                    System.out.println(cons.getString());
                }
            }

            return newValue(Type.getReturnType(methodInsn.desc));
        }
    }

    @Override
    public void returnOperation(AbstractInsnNode insn, Value value, Value expected) throws AnalyzerException {
    }

    @Override
    public Value merge(Value v, Value w) {
        if (v != w) {
            return UNINITIALIZED_VALUE;
        } else {
            return v;
        }
    }
}
