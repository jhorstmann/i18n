package net.jhorstmann.i18n.tools.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public class NestedContentHandler extends DefaultContentHandler {

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
    private final XMLReader xmlreader;
    private HandlerChain handlers;

    public NestedContentHandler(NestedContentHandler parent) {
        this(parent.xmlreader);
    }

    public NestedContentHandler(XMLReader xmlreader, ContentHandler handler) {
        this.xmlreader = xmlreader;
        this.handlers = new HandlerChain(handler);
    }

    public NestedContentHandler(XMLReader xmlreader) {
        this.xmlreader = xmlreader;
    }

    public final XMLReader getXMLReader() {
        return xmlreader;
    }

    public final void pushHandler(ContentHandler handler) {
        handlers = new HandlerChain(handler, handlers);
        xmlreader.setContentHandler(handler);
        if (handler instanceof LexicalHandler) {
            try {
                xmlreader.setProperty("http://xml.org/sax/properties/lexical-handler", handler);
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
        xmlreader.setContentHandler(handlers.handler);
    }
}
