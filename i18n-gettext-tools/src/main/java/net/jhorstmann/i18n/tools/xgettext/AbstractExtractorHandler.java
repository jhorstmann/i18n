package net.jhorstmann.i18n.tools.xgettext;

import net.jhorstmann.i18n.tools.xml.NamespaceContextImpl;
import net.jhorstmann.i18n.tools.xml.NestedContentHandler;
import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import net.jhorstmann.i18n.tools.MessageBundle;

import org.fedorahosted.tennera.jgettext.Message;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class AbstractExtractorHandler extends NestedContentHandler implements Locator, NamespaceContext {
    
    private final NamespaceContextImpl namespaceContext;
    private final MessageBundle bundle;
    private Locator locator;

    public AbstractExtractorHandler(XMLReader xmlreader, MessageBundle bundle) {
        super(xmlreader);
        this.bundle = bundle;
        this.namespaceContext = new NamespaceContextImpl();
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
    
    public Message addMessage(String msgid) {
        Message msg = createMessage(msgid);
        msg.setMsgstr(msgid);
        bundle.addMessage(msg);
        return msg;
    }
    
    public Message addMessage(String context, String message, String plural, String comment) {
        if (message == null) {
            throw new IllegalArgumentException("Message id must not be null");
        }
        Message msg = createMessage(message);
        if (context != null && context.length() > 0) {
            msg.setMsgctxt(context);
        }
        if (plural != null && plural.length() > 0) {
            msg.setMsgidPlural(plural);
            msg.addMsgstrPlural("", 0);
        }
        if (comment != null && comment.length() > 0) {
            msg.addExtractedComment(comment);
        }
        
        bundle.addMessage(msg);
        return msg;
    }
    
    public Message addMessageWithContext(String msgctx, String msgid) {
        Message msg = createMessage(msgid);
        msg.setMsgctxt(msgctx);
        bundle.addMessage(msg);
        return msg;
    }
    
    public Message addMessageWithPlural(String msgidSingular, String msgidPlural) {
        Message msg = createMessage(msgidSingular);
        msg.setMsgidPlural(msgidPlural);
        bundle.addMessage(msg);
        return msg;
    }
    
    public Message addMessageWithContextAndPlural(String msgctx, String msgidSingular, String msgidPlural) {
        Message msg = createMessage(msgidSingular);
        msg.setMsgctxt(msgctx);
        msg.setMsgidPlural(msgidPlural);
        bundle.addMessage(msg);
        return msg;
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
