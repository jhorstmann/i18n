package net.jhorstmann.i18n.tools.xgettext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageFunction {

    private static final Pattern SIGNATURE_PATTERN;
    static {
        String name = "\\w+";
        String packagePrefix = "(?:" + name + "\\.)*";
        String methodName = packagePrefix + "(?:" + name + "|<init>)";
        String typeName = "(?:" + packagePrefix + name + "(?:\\[\\])*(?:\\.\\.\\.)?)";
        String sp = "\\s+";
        String param = typeName + "(?:" + sp + name + ")?";
        String paramList = param + "(?:\\s*,\\s*" + param + ")*";
        SIGNATURE_PATTERN = Pattern.compile("(?:(" + typeName + ")" + sp + ")?" + "(" + methodName + ")\\(((?:" + paramList + ")?)\\)");
    }

    private String namespace;
    private String name;
    private String description;
    private int parameterCount;
    private int messageIndex;
    private int contextIndex;
    private int pluralIndex;
    
    private static int validateParamIdx(String signature) {
        int idx = signature.indexOf('(');
        if (idx < 0) {
            throw new IllegalArgumentException("Invalid function signature");
        } else if (idx == 0) {
            throw new IllegalArgumentException("Missing function name");
        }
        if (!signature.endsWith(")")) {
            throw new IllegalArgumentException("Invalid function signature");
        }
        return idx;
    }
    
    public static MessageFunction fromEL(String signature) {
        String namespace;
        if (signature.charAt(0) == '{') {
            int idx = signature.indexOf('}', 1);
            namespace = signature.substring(1, idx);
            signature = signature.substring(idx+1);
        } else {
            namespace = "";
        }

        return fromEL(namespace, signature);
    }

    public static MessageFunction fromEL(String namespace, String signature) {
        int idx = validateParamIdx(signature);
        String methodName = signature.substring(0, idx).trim();
        String methodParams = signature.substring(idx + 1, signature.length() - 1);
        int messageIndex = -1;
        int contextIndex = -1;
        int pluralIndex = -1;
        String[] params = methodParams.split("\\s*,\\s*");
        int length = params.length;
        for (int i = 0; i < length; i++) {
            String name = params[i];
            if ("context".equals(name)) {
                contextIndex = i;
            } else if ("message".equals(name)) {
                messageIndex = i;
            } else if ("plural".equals(name)) {
                pluralIndex = i;
            }
        }
        return new MessageFunction(namespace, methodName, null, messageIndex, contextIndex, pluralIndex, length);
    }

    public static MessageFunction fromJava(String signature) {
        Matcher matcher = SIGNATURE_PATTERN.matcher(signature);
        if (matcher.matches()) {
            String returnType = matcher.group(1);
            String methodName = matcher.group(2);
            String parameters = matcher.group(3);
            String className;
            int idx = methodName.lastIndexOf('.');
            if (idx >= 0) {
                className = methodName.substring(0, idx);
                methodName = methodName.substring(idx+1);
            } else {
                className = "";
            }
            if (returnType == null) {
                returnType = "void";
            }
            return fromJava(className, returnType, methodName, parameters);

        } else {
            throw new IllegalArgumentException("Invalid java method signature '" + signature + "'");
        }
    }

    public static MessageFunction fromJava(String className, String signature) {
        MessageFunction fn = fromJava(signature);
        fn.setNamespace(className.replace('.', '/'));
        return fn;
    }

    private static void appendInternalName(StringBuilder desc, String javaType) {
        if (javaType.endsWith("...")) {
            desc.append("[");
            javaType = javaType.substring(0, javaType.length() - 3);
        }
        while (javaType.endsWith("[]")) {
            desc.append("[");
            javaType = javaType.substring(0, javaType.length() - 2);
        }
        if ("void".equals(javaType)) {
            desc.append("V");
        } else if ("boolean".equals(javaType)) {
            desc.append("Z");
        } else if ("byte".equals(javaType)) {
            desc.append("B");
        } else if ("char".equals(javaType)) {
            desc.append("C");
        } else if ("short".equals(javaType)) {
            desc.append("S");
        } else if ("int".equals(javaType)) {
            desc.append("I");
        } else if ("long".equals(javaType)) {
            desc.append("J");
        } else if ("float".equals(javaType)) {
            desc.append("F");
        } else if ("double".equals(javaType)) {
            desc.append("D");
        } else if ("Object".equals(javaType)) {
            desc.append("Ljava/lang/Object;");
        } else if ("String".equals(javaType)) {
            desc.append("Ljava/lang/String;");
        } else if ("Locale".equals(javaType)) {
            desc.append("Ljava/util/Locale;");
        } else if ("ResourceBundle".equals(javaType)) {
            desc.append("Ljava/util/ResourceBundle;");
        } else {
            desc.append("L");
            desc.append(javaType.replace('.', '/'));
            desc.append(";");
        }
    }

    private static MessageFunction fromJava(String className, String returnType, String methodName, String methodParams) {
        String namespace = className.replace('.', '/');
        int messageIndex = -1;
        int contextIndex = -1;
        int pluralIndex = -1;
        String[] params = methodParams.split("\\s*,\\s*");
        StringBuilder desc = new StringBuilder();
        desc.append("(");
        int length = params.length;
        for (int i = 0; i < length; i++) {
            String[] typeAndName = params[i].split("\\s+");
            if (typeAndName.length > 1) {
                String name = typeAndName[1];
                if ("context".equals(name)) {
                    contextIndex = i;
                } else if ("message".equals(name)) {
                    messageIndex = i;
                } else if ("plural".equals(name)) {
                    pluralIndex = i;
                }
            }
            if (typeAndName[0].length() > 0) {
                appendInternalName(desc, typeAndName[0]);
            }
        }
        desc.append(")");
        appendInternalName(desc, returnType);
        return new MessageFunction(namespace, methodName, desc.toString(), messageIndex, contextIndex, pluralIndex, length);
    }

    private MessageFunction(String namespace, String name, String description, int messageIndex, int contextIndex, int pluralIndex, int parameterCount) {
        this.namespace = namespace;
        this.name = name;
        this.description = description;
        this.messageIndex = messageIndex;
        this.contextIndex = contextIndex;
        this.pluralIndex = pluralIndex;
        this.parameterCount = parameterCount;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getContextIndex() {
        return contextIndex;
    }

    public void setContextIndex(int contextIndex) {
        this.contextIndex = contextIndex;
    }

    public int getMessageIndex() {
        return messageIndex;
    }

    public void setMessageIndex(int messageIndex) {
        this.messageIndex = messageIndex;
    }

    public int getPluralIndex() {
        return pluralIndex;
    }

    public void setPluralIndex(int pluralIndex) {
        this.pluralIndex = pluralIndex;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public void setParameterCount(int parameterCount) {
        this.parameterCount = parameterCount;
    }
}
