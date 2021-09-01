package net.jhorstmann.i18n.xgettext.web;

import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.xgettext.AbstractExtractorHandler;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractor;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractorException;
import net.jhorstmann.i18n.tools.xgettext.MessageFunction;
import net.jhorstmann.i18n.tools.xml.XMLHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.el.ELException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WebMessageExtractor extends AbstractExtractorHandler implements MessageExtractor {

    private static final Logger log = LoggerFactory.getLogger(WebMessageExtractor.class);

    public static final String DEFAULT_NAMESPACE = "http://jhorstmann.net/taglib/i18n";
    public static final List<MessageFunction> DEFAULT_MESSAGE_FUNCTIONS;
    static {
        List<MessageFunction> functions = new ArrayList<MessageFunction>();
        String ns = DEFAULT_NAMESPACE;
        functions.add(MessageFunction.fromEL(ns, "mark(message)"));
        functions.add(MessageFunction.fromEL(ns, "tr(message)"));
        functions.add( MessageFunction.fromEL( ns, "tr1(message, param1)" ) );
        functions.add( MessageFunction.fromEL( ns, "tr2(message, param1, param2)" ) );
        functions.add( MessageFunction.fromEL( ns, "tr3(message, param1, param2, param3)" ) );
        functions.add( MessageFunction.fromEL( ns, "tr4(message, param1, param2, param3, param4)" ) );
        functions.add(MessageFunction.fromEL(ns, "trc(context, message)"));
        functions.add( MessageFunction.fromEL( ns, "trc1(context, message, param1)" ) );
        functions.add( MessageFunction.fromEL( ns, "trc2(context, message, param1, param2)" ) );
        functions.add( MessageFunction.fromEL( ns, "trc3(context, message, param1, param2, param3)" ) );
        functions.add( MessageFunction.fromEL( ns, "trc4(context, message, param1, param2, param3, param4)" ) );
        functions.add(MessageFunction.fromEL(ns, "trn(message, plural, long)"));
        functions.add( MessageFunction.fromEL( ns, "trn1(message, plural, long, param1)" ) );
        functions.add( MessageFunction.fromEL( ns, "trn2(message, plural, long, param1, param2)" ) );
        functions.add( MessageFunction.fromEL( ns, "trn3(message, plural, long, param1, param2, param3)" ) );
        functions.add( MessageFunction.fromEL( ns, "trn4(message, plural, long, param1, param2, param3, param4)" ) );
        functions.add(MessageFunction.fromEL(ns, "trnc(context, message, plural, long)"));
        functions.add( MessageFunction.fromEL( ns, "trnc1(context, message, plural, long, param1)" ) );
        functions.add( MessageFunction.fromEL( ns, "trnc2(context, message, plural, long, param1, param2)" ) );
        functions.add( MessageFunction.fromEL( ns, "trnc3(context, message, plural, long, param1, param2, param3)" ) );
        functions.add( MessageFunction.fromEL( ns, "trnc4(context, message, plural, long, param1, param2, param3, param4)" ) );

        DEFAULT_MESSAGE_FUNCTIONS = Collections.unmodifiableList(functions);
    }

    private List<MessageFunction> functions;

    public WebMessageExtractor(MessageBundle bundle) {
        this(bundle, DEFAULT_MESSAGE_FUNCTIONS, null, false, true);
    }

    public WebMessageExtractor(MessageBundle bundle, File rootDir, boolean relativizeSrcRefPaths, boolean srcRefPaths) {
        this(bundle, DEFAULT_MESSAGE_FUNCTIONS, rootDir, relativizeSrcRefPaths, srcRefPaths);
    }

    public WebMessageExtractor(MessageBundle bundle, List<MessageFunction> functions) {
        this(bundle, functions, null, false, true);
    }

    public WebMessageExtractor(MessageBundle bundle, List<MessageFunction> functions, File rootDir, boolean relativizeSrcRefPaths, boolean srcRefPaths) {
        super(XMLHelper.createXMLReader(), bundle, rootDir, relativizeSrcRefPaths, srcRefPaths);
        this.functions = functions;
    }

    public List<MessageFunction> getFunctions() {
        return functions;
    }

    @Override
    public void startDocument() throws SAXException {
        pushHandler(new RootHandler(this));
    }

    @Override
    public void endDocument() throws SAXException {
        popHandler();
    }

    @Override
    public final void extractMessages(File file) throws IOException, MessageExtractorException {
        String systemId = file.getAbsolutePath();
        log.debug("Extracting messages from {}", systemId);
        InputStream in = new FileInputStream(file);
        try {
            InputSource input = new InputSource(in);
            input.setSystemId(systemId);
            extractMessages(input);
        } finally {
            in.close();
        }
    }

    void extractMessages(InputSource input) throws IOException, MessageExtractorException {
        XMLReader xmlreader = getXMLReader();
        xmlreader.setContentHandler(this);
        try {
            xmlreader.parse(input);
        } catch (SAXException ex) {
            throw new MessageExtractorException(input.getSystemId(), ex);
        } catch (ELException ex) {
            throw new MessageExtractorException(input.getSystemId(), ex);
        }
    }
}
