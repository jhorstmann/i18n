package net.jhorstmann.i18n.xgettext.asm;

import org.objectweb.asm.tree.ClassNode;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.fedorahosted.tennera.jgettext.Message;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import java.util.List;
import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.xgettext.MessageFunction;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;
import org.slf4j.LoggerFactory;

import static org.objectweb.asm.Opcodes.*;

public class ConstantTrackingInterpreter implements Interpreter {
    private static final Logger log = LoggerFactory.getLogger(ConstantTrackingInterpreter.class);
    private static final Value NULL_VALUE = new ConstantNullValue();
    private static final Value INT_VALUE = new SimpleValue(Type.INT_TYPE);
    private static final Value LONG_VALUE = new SimpleValue(Type.LONG_TYPE);
    private static final Value FLOAT_VALUE = new SimpleValue(Type.FLOAT_TYPE);
    private static final Value DOUBLE_VALUE = new SimpleValue(Type.DOUBLE_TYPE);
    private static final Value REFERENCE_VALUE = new SimpleValue(Type.getObjectType("java/lang/Object"));
    private static final Value UNINITIALIZED_VALUE = new UninitializedValue();
    private static final Value RETURN_ADDR_VALUE = new ReturnAddressValue();
    private MessageBundle bundle;
    private Map<String, MessageFunction> functionByDesc;
    private ClassNode currentClass;
    private MethodNode currentMethod;

    public ConstantTrackingInterpreter(MessageBundle catalog, List<MessageFunction> functions) {
        this.bundle = catalog;
        this.functionByDesc = new HashMap<String, MessageFunction>();
        for (MessageFunction function : functions) {
            String key = function.getNamespace() + "." + function.getName() + function.getDescription();
            log.debug("Adding function {}", key);
            if (functionByDesc.containsKey(key)) {
                log.error("Multiple definitions for MessageFunction {}" + key);
            }
            functionByDesc.put(key, function);
        }
    }

    public ClassNode getCurrentClass() {
        return currentClass;
    }

    public void setCurrentClass(ClassNode currentClass) {
        this.currentClass = currentClass;
    }
    
    public void setCurrentMethod(MethodNode methodNode) {
        this.currentMethod = methodNode;
    }
    
    public MethodNode getCurrentMethod() {
        return currentMethod;
    }
    
    public String getCurrentSourceReference() {
        String methodName = currentMethod == null ? null : (currentMethod.name + currentMethod.desc);
        if (currentClass == null) {
            return methodName;
        } else {
            return methodName == null ? currentClass.name : (currentClass.name + "." + methodName);
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
                    return INT_VALUE;
                case Type.FLOAT:
                    return FLOAT_VALUE;
                case Type.LONG:
                    return LONG_VALUE;
                case Type.DOUBLE:
                    return DOUBLE_VALUE;
                case Type.ARRAY:
                    return REFERENCE_VALUE;
                case Type.OBJECT:
                    return new SimpleValue(type);
                default:
                    throw new IllegalStateException("Unhandled type " + type);
            }
        }
    }

    @Override
    public Value newOperation(final AbstractInsnNode insn) throws AnalyzerException {
        int opcode = insn.getOpcode();
        //System.out.println("newOperation(" + opcode + ")");
        switch (opcode) {
            case ACONST_NULL:
                return NULL_VALUE;
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
                return INT_VALUE;
            case LCONST_0:
            case LCONST_1:
                return LONG_VALUE;
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
                return FLOAT_VALUE;
            case DCONST_0:
            case DCONST_1:
                return DOUBLE_VALUE;
            case BIPUSH:
            case SIPUSH:
                return INT_VALUE;
            case LDC:
                Object cst = ((LdcInsnNode) insn).cst;
                if (cst instanceof String) {
                    return new ConstantStringValue((String) cst);
                } else if (cst instanceof Type) {
                    return new ConstantTypeValue((Type) cst);
                } else if (cst instanceof Integer) {
                    return INT_VALUE;
                } else if (cst instanceof Long) {
                    return LONG_VALUE;
                } else if (cst instanceof Float) {
                    return FLOAT_VALUE;
                } else if (cst instanceof Double) {
                    return DOUBLE_VALUE;
                } else {
                    return newValue(Type.getType(cst.getClass()));
                }
            case JSR:
                return RETURN_ADDR_VALUE;
            case GETSTATIC:
                return newValue(Type.getType(((FieldInsnNode) insn).desc));
            case NEW:
                return newValue(Type.getObjectType(((TypeInsnNode) insn).desc));
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
        //System.out.println("unaryOperation(" + opcode + ")");
        switch (opcode) {
            case INEG:
            case IINC:
            case L2I:
            case F2I:
            case D2I:
            case I2B:
            case I2C:
            case I2S:
                return INT_VALUE;
            case FNEG:
            case I2F:
            case L2F:
            case D2F:
                return FLOAT_VALUE;
            case LNEG:
            case I2L:
            case F2L:
            case D2L:
                return LONG_VALUE;
            case DNEG:
            case I2D:
            case L2D:
            case F2D:
                return DOUBLE_VALUE;
            case GETFIELD:
                return newValue(Type.getType(((FieldInsnNode) insn).desc));
            case NEWARRAY:
            case ANEWARRAY:
                return REFERENCE_VALUE;
            case ARRAYLENGTH:
                return INT_VALUE;
            case CHECKCAST:
                return newValue(Type.getObjectType(((TypeInsnNode) insn).desc));
            case INSTANCEOF:
                return INT_VALUE;
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
                return INT_VALUE;
            case FALOAD:
            case FADD:
            case FSUB:
            case FMUL:
            case FDIV:
            case FREM:
                return FLOAT_VALUE;
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
                return LONG_VALUE;
            case DALOAD:
            case DADD:
            case DSUB:
            case DMUL:
            case DDIV:
            case DREM:
                return DOUBLE_VALUE;
            case AALOAD:
                return REFERENCE_VALUE;
            case LCMP:
            case FCMPL:
            case FCMPG:
            case DCMPL:
            case DCMPG:
                return INT_VALUE;
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

    private static boolean equals(String str1, String str2) {
        return str1 == null ? str2 == null : str1.equals(str2);
    }

    private void extractMessage(MessageFunction function, String functionName, List values) {
        String sourceReference = getCurrentSourceReference();
        int length = values.size();
        int messageIdx = function.getMessageIndex();
        int contextIdx = function.getContextIndex();
        int pluralIdx = function.getPluralIndex();
        String message = null;
        String context = null;
        String plural = null;
        if (messageIdx >= 0 && messageIdx < length) {
            AbstractValue value = (AbstractValue) values.get(messageIdx);
            if (value instanceof ConstantStringValue) {
                message = ((ConstantStringValue) value).getConstantValue();
            } else {
                log.warn("Message id in parameter {} for function {} in class {} is not constant", new Object[]{messageIdx, functionName, sourceReference});
                return;
            }
        }
        if (contextIdx >= 0 && contextIdx < length) {
            AbstractValue value = (AbstractValue) values.get(contextIdx);
            if (value instanceof ConstantStringValue) {
                context = ((ConstantStringValue) value).getConstantValue();
            } else {
                log.warn("Message context in parameter {} for function {} in class {} is not constant", new Object[]{contextIdx, functionName, sourceReference});
                return;
            }
        }
        if (pluralIdx >= 0 && pluralIdx < length) {
            AbstractValue value = (AbstractValue) values.get(pluralIdx);
            if (value instanceof ConstantStringValue) {
                plural = ((ConstantStringValue) value).getConstantValue();
            } else {
                log.warn("Message plural in parameter {} for function {} in class {} is not constant", new Object[]{pluralIdx, functionName, sourceReference});
                return;
            }
        }

        if (message != null) {

            Message msg = new Message();
            msg.setMsgid(message);
            if (context != null) {
                msg.setMsgctxt(context);
            }
            if (plural != null) {
                msg.setMsgidPlural(plural);
                msg.addMsgstrPlural("", 0);
            }
            if (sourceReference != null) {
                msg.addSourceReference(sourceReference);
            }
            log.debug("Found message {}", msg);

            bundle.addMessage(msg);
        }
    }

    @Override
    public Value naryOperation(AbstractInsnNode insn, List values) throws AnalyzerException {
        int opcode = insn.getOpcode();
        if (opcode == MULTIANEWARRAY) {
            return REFERENCE_VALUE;
        } else {
            MethodInsnNode methodInsn = (MethodInsnNode) insn;
            String owner = methodInsn.owner;
            String name = methodInsn.name;
            String desc = methodInsn.desc;

            if (opcode == INVOKEVIRTUAL) {
                AbstractValue thisValue = (AbstractValue) values.get(0);
                if (thisValue instanceof ConstantTypeValue) {
                    Type thisType = ((ConstantTypeValue) thisValue).getConstantType();
                    if ("java/lang/Class".equals(owner) && "()Ljava/lang/String;".equals(desc)) {
                        if ("getName".equals(name)) {
                            return new ConstantStringValue(thisType.getClassName());
                        } else if ("getSimpleName".equals(name)) {
                            String className = thisType.getClassName();
                            int idx = Math.max(className.lastIndexOf("."), className.lastIndexOf('$'));
                            return new ConstantStringValue(idx >= 0 ? className.substring(idx + 1) : className);
                        }
                    }
                } else if (thisValue instanceof ConstantStringValue) {
                    String thisString = ((ConstantStringValue) thisValue).getConstantValue();
                    if ("java/lang/String".equals(owner)) {
                        if ("concat".equals(name) && "(Ljava/lang/String;)Ljava/lang/String;".equals(desc)) {
                            AbstractValue otherValue = (AbstractValue) values.get(1);
                            if (otherValue instanceof ConstantStringValue) {
                                String otherString = ((ConstantStringValue) otherValue).getConstantValue();
                                return new ConstantStringValue(thisString.concat(otherString));
                            }
                        }
                    }
                }
            }
            

            String key = owner + "." + name + desc;
            log.debug("Checking for functions matching {}", key);
            MessageFunction function = functionByDesc.get(key);
            if (function != null) {
                log.debug("Extracting messages from call to " + key);
                extractMessage(function, key, values);
            }

            return newValue(Type.getReturnType(methodInsn.desc));
        }
    }

    @Override
    public void returnOperation(AbstractInsnNode insn, Value value, Value expected) throws AnalyzerException {
    }

    @Override
    public Value merge(Value v, Value w) {
        if (!v.equals(w)) {
            return UNINITIALIZED_VALUE;
        }
        return v;
    }
}
