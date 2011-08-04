package net.jhorstmann.i18n.xgettext.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.el.ELException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import net.jhorstmann.i18n.xgettext.MessageExtractor;
import net.jhorstmann.i18n.xgettext.MessageExtractorException;
import net.jhorstmann.i18n.xgettext.MessageFunction;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class WebMessageExtractor implements MessageExtractor {

    private static final Logger log = LoggerFactory.getLogger(WebMessageExtractor.class);

    public static final String DEFAULT_NAMESPACE = "http://jhorstmann.net/taglib/i18n";
    public static final List<MessageFunction> DEFAULT_MESSAGE_FUNCTIONS;
    static {
        List<MessageFunction> functions = new ArrayList<MessageFunction>();
        String ns = DEFAULT_NAMESPACE;
        functions.add(MessageFunction.fromEL(ns, "mark(message)"));
        functions.add(MessageFunction.fromEL(ns, "tr(message)"));
        functions.add(MessageFunction.fromEL(ns, "trc(context, message)"));
        functions.add(MessageFunction.fromEL(ns, "trn(message, plural, long)"));
        functions.add(MessageFunction.fromEL(ns, "trnc(context, message, plural, long)"));

        DEFAULT_MESSAGE_FUNCTIONS = Collections.unmodifiableList(functions);
    }

    
    private Catalog catalog;
    private XMLReader xmlreader;
    private List<MessageFunction> functions;

    public WebMessageExtractor(Catalog catalog) {
        this(catalog, DEFAULT_MESSAGE_FUNCTIONS);
    }
    
    public WebMessageExtractor(Catalog catalog, List<MessageFunction> functions) {
        this.catalog = catalog;
        this.xmlreader = createXMLReader();
        this.functions = functions;
    }
    
    private static SAXParserFactory createParserFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        try {
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (ParserConfigurationException ex) {
        } catch (SAXNotRecognizedException ex) {
        } catch (SAXNotSupportedException ex) {
        }
        try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException ex) {
        } catch (SAXNotRecognizedException ex) {
        } catch (SAXNotSupportedException ex) {
        }
        return factory;
    }
    
    private static XMLReader createXMLReader() {
        SAXParserFactory factory = createParserFactory();
        try {
            SAXParser parser = factory.newSAXParser();
            return parser.getXMLReader();
        } catch (ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        } catch (SAXException ex) {
            throw new IllegalStateException(ex);
        }
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
        xmlreader.setContentHandler(new ExtractorHandler(xmlreader, catalog, functions));
        try {
            xmlreader.parse(input);
        } catch (SAXException ex) {
            throw new MessageExtractorException(ex);
        } catch (ELException ex) {
            throw new MessageExtractorException(ex);
        }
    }
}
