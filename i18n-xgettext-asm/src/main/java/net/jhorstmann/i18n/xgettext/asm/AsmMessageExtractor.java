package net.jhorstmann.i18n.xgettext.asm;

import net.jhorstmann.i18n.xgettext.MessageFunction;
import net.jhorstmann.i18n.I18N;
import net.jhorstmann.i18n.xgettext.MessageExtractor;
import net.jhorstmann.i18n.xgettext.MessageExtractorException;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AsmMessageExtractor implements MessageExtractor {

    public static final List<MessageFunction> DEFAULT_MESSAGE_FUNCTIONS;
    static {
        List<MessageFunction> functions = new ArrayList<MessageFunction>();
        String namespace = I18N.class.getName();
        functions.add(MessageFunction.fromJava(namespace, "String tr(String message, Object...)"));
        functions.add(MessageFunction.fromJava(namespace, "String tr(Locale, String message, Object...)"));
        functions.add(MessageFunction.fromJava(namespace, "String tr(ResourceBundle, String message, Object...)"));

        functions.add(MessageFunction.fromJava(namespace, "String trc(String context, String message, Object...)"));
        functions.add(MessageFunction.fromJava(namespace, "String trc(Locale, String context, String message, Object...)"));
        functions.add(MessageFunction.fromJava(namespace, "String trc(ResourceBundle, String context, String message, Object...)"));
        
        functions.add(MessageFunction.fromJava(namespace, "String trn(String message, String plural, long, Object...)"));
        functions.add(MessageFunction.fromJava(namespace, "String trn(Locale, String message, String plural, long, Object...)"));
        functions.add(MessageFunction.fromJava(namespace, "String trn(ResourceBundle, String message, String plural, long, Object...)"));
        
        functions.add(MessageFunction.fromJava(namespace, "String trnc(String context, String message, String plural, long, Object...)"));
        functions.add(MessageFunction.fromJava(namespace, "String trnc(Locale, String context, String message, String plural, long, Object...)"));
        functions.add(MessageFunction.fromJava(namespace, "String trnc(ResourceBundle, String context, String message, String plural, long, Object...)"));

        functions.add(MessageFunction.fromJava(namespace, "String mark(String message)"));

        DEFAULT_MESSAGE_FUNCTIONS = Collections.unmodifiableList(functions);
    }
    private ConstantTrackingInterpreter interpreter;

    public AsmMessageExtractor(Catalog catalog) {
        this(catalog, DEFAULT_MESSAGE_FUNCTIONS);
    }

    public AsmMessageExtractor(Catalog catalog, List<MessageFunction> functions) {
        this.interpreter = new ConstantTrackingInterpreter(catalog, functions);
    }

    @Override
    public final void extractMessages(File file) throws IOException, MessageExtractorException {
        InputStream in = new FileInputStream(file);
        extractMessages(in, true);
    }

    public final void extractMessages(InputStream in, boolean close) throws IOException, MessageExtractorException {
        try {
            extractMessages(in);
        } finally {
            if (close) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public final void extractMessages(InputStream in) throws IOException, MessageExtractorException {
        ClassReader classReader = new ClassReader(in);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);

        interpreter.setCurrentClass(classNode);
        Analyzer analyzer = new Analyzer(interpreter);

        try {
            for (Iterator it=classNode.methods.iterator(); it.hasNext(); ) {
                MethodNode methodNode = (MethodNode) it.next();
                interpreter.setCurrentMethod(methodNode);
                analyzer.analyze(classNode.name, methodNode);
            }
        } catch (AnalyzerException ex) {
            throw new MessageExtractorException(ex);
        }
    }
}
