package net.jhorstmann.i18n.xgettext.web;

import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import net.jhorstmann.i18n.xgettext.MessageFunction;
import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class ExtractorHandler extends NestedContentHandler implements Locator, NamespaceContext {
    private static final Logger log = LoggerFactory.getLogger(ExtractorHandler.class);
    
    private final NamespaceContextImpl namespaceContext;
    private final Catalog catalog;
    private Locator locator;
    private List<MessageFunction> functions;

    public ExtractorHandler(XMLReader xmlreader, Catalog catalog, List<MessageFunction> functions) {
        super(xmlreader);
        this.catalog = catalog;
        this.namespaceContext = new NamespaceContextImpl();
        this.functions = functions;
    }

    List<MessageFunction> getFunctions() {
        return functions;
    }
    
    private Message createMessage(String msgid) {
        Message msg = new Message();
        if (locator != null) {
            String systemId = locator.getSystemId();
            if (systemId != null) {
                int line = locator.getLineNumber();
                if (line > 0) {
                    msg.addSourceReference(systemId, line);
                } else {
                    msg.addSourceReference(systemId);
                }
            }
        }
        msg.setMsgid(msgid);
        //msg.setFuzzy(true);
        return msg;
    }
    
    public void addMessage(String msgid) {
        catalog.addMessage(createMessage(msgid));
    }
    
    public void addMessage(String context, String message, String plural, String comment) {
        if (message == null) {
            throw new IllegalArgumentException("Message id must not be null");
        }
        Message msg = createMessage(message);
        if (context != null && context.length() > 0) {
            msg.setMsgctxt(context);
        }
        if (plural != null && plural.length() > 0) {
            msg.setMsgidPlural(plural);
            msg.addMsgstrPlural(message, 0);
            msg.addMsgstrPlural(plural, 1);
        }
        if (comment != null && comment.length() > 0) {
            msg.addExtractedComment(comment);
        }
        
        catalog.addMessage(msg);
    }
    
    public void addMessageWithContext(String msgctx, String msgid) {
        Message msg = createMessage(msgid);
        msg.setMsgctxt(msgctx);
        catalog.addMessage(msg);
    }
    
    public void addMessageWithPlural(String msgidSingular, String msgidPlural) {
        Message msg = createMessage(msgidSingular);
        msg.setMsgidPlural(msgidPlural);
        catalog.addMessage(msg);
    }
    
    public void addMessageWithContextAndPlural(String msgctx, String msgidSingular, String msgidPlural) {
        Message msg = createMessage(msgidSingular);
        msg.setMsgctxt(msgctx);
        msg.setMsgidPlural(msgidPlural);
        catalog.addMessage(msg);
    }
    
    @Override
    public void startDocument() throws SAXException {
        pushHandler(new ELHandler(this));
    }

    @Override
    public void endDocument() throws SAXException {
        popHandler();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        namespaceContext.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        namespaceContext.endPrefixMapping(prefix);
    }

    @Override
    public String getNamespaceURI(String prefix) {
        return namespaceContext.getNamespaceURI(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        return namespaceContext.getPrefix(namespaceURI);
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        return namespaceContext.getPrefixes(namespaceURI);
    }

    public QName resolveQName(String name) {
        return namespaceContext.resolveQName(name);
    }

    @Override
    public int getLineNumber() {
        return locator != null ? locator.getLineNumber() : -1;
    }

    @Override
    public int getColumnNumber() {
        return locator != null ? locator.getColumnNumber() : -1;
    }

    @Override
    public String getSystemId() {
        return locator != null ? locator.getSystemId() : null;
    }

    @Override
    public String getPublicId() {
        return locator != null ? locator.getPublicId() : null;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

}
