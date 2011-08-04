package net.jhorstmann.i18n.xgettext.web;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class XMLReaderAdapter implements XMLReader {
    public static final String SAX_FEATURE_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    public static final String SAX_FEATURE_EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    public static final String SAX_FEATURE_EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    public static final String XERCES_FEATURE_LOAD_DTD_GRAMMAR = "http://apache.org/xml/features/nonvalidating/load-dtd-grammar";
    public static final String XERCES_FEATURE_LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    static class HandlerChain {

        ContentHandler handler;
        HandlerChain parent;

        HandlerChain(ContentHandler handler) {
            this.handler = handler;
        }

        HandlerChain(ContentHandler handler, HandlerChain parent) {
            this.handler = handler;
            this.parent = parent;
        }
    }
    
    public static XMLReaderAdapter newInstance() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        SAXParser parser = factory.newSAXParser();
        return new XMLReaderAdapter(parser.getXMLReader());
    }

    public static XMLReaderAdapter newInstanceWithoutDTD() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        try {
            factory.setFeature(SAX_FEATURE_EXTERNAL_GENERAL_ENTITIES, false);
            factory.setFeature(SAX_FEATURE_EXTERNAL_PARAMETER_ENTITIES, false);
        } catch (ParserConfigurationException ex) {
        } catch (SAXNotRecognizedException ex) {
        } catch (SAXNotSupportedException ex) {
        }
        try {
            factory.setFeature(XERCES_FEATURE_LOAD_DTD_GRAMMAR, false);
            factory.setFeature(XERCES_FEATURE_LOAD_EXTERNAL_DTD, false);
        } catch (ParserConfigurationException ex) {
        } catch (SAXNotRecognizedException ex) {
        } catch (SAXNotSupportedException ex) {
        }
        SAXParser parser = factory.newSAXParser();
        return new XMLReaderAdapter(parser.getXMLReader());
    }

    private XMLReader xmlreader;
    private HandlerChain handlers;

    public XMLReaderAdapter(XMLReader xmlreader) {
        this.xmlreader = xmlreader;
    }

    public XMLReaderAdapter(XMLReader xmlreader, ContentHandler handler) {
        this.xmlreader = xmlreader;
        pushHandler(handler);
    }

    public final void pushHandler(ContentHandler handler) {
        handlers = new HandlerChain(handler, handlers);
        setContentHandler(handler);
        if (handler instanceof LexicalHandler) {
            try {
                setLexicalHandler(((LexicalHandler)handler));
            } catch (SAXNotSupportedException ex) {
            } catch (SAXNotRecognizedException ex) {
            }
        }
    }

    public final void popHandler() {
        if (handlers == null) {
            throw new IllegalStateException("No handlers on stack");
        }
        handlers = handlers.parent;
        setContentHandler(handlers.handler);
    }

    public void setLexicalHandler(LexicalHandler handler) throws SAXNotRecognizedException, SAXNotSupportedException {
        xmlreader.setProperty(SAX_FEATURE_LEXICAL_HANDLER, handler);
    }
    
    public LexicalHandler getLexicalHandler() throws SAXNotRecognizedException, SAXNotSupportedException {
        return (LexicalHandler) xmlreader.getProperty(SAX_FEATURE_LEXICAL_HANDLER);
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        xmlreader.setProperty(name, value);
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        xmlreader.setFeature(name, value);
    }
    

    public void setErrorHandler(ErrorHandler handler) {
        xmlreader.setErrorHandler(handler);
    }

    public void setEntityResolver(EntityResolver resolver) {
        xmlreader.setEntityResolver(resolver);
    }

    public void setDTDHandler(DTDHandler handler) {
        xmlreader.setDTDHandler(handler);
    }

    public void setContentHandler(ContentHandler handler) {
        xmlreader.setContentHandler(handler);
    }

    public void parse(String systemId) throws IOException, SAXException {
        xmlreader.parse(systemId);
    }

    public void parse(InputSource input) throws IOException, SAXException {
        xmlreader.parse(input);
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return xmlreader.getProperty(name);
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        return xmlreader.getFeature(name);
    }

    public ErrorHandler getErrorHandler() {
        return xmlreader.getErrorHandler();
    }

    public EntityResolver getEntityResolver() {
        return xmlreader.getEntityResolver();
    }

    public DTDHandler getDTDHandler() {
        return xmlreader.getDTDHandler();
    }

    public ContentHandler getContentHandler() {
        return xmlreader.getContentHandler();
    }
}
