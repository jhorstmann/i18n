package net.jhorstmann.i18n.tools.xgettext;

import java.io.File;
import java.net.URLDecoder;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.ResourceUtils;
import net.jhorstmann.i18n.tools.xml.NamespaceContextImpl;
import net.jhorstmann.i18n.tools.xml.NestedContentHandler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.fedorahosted.tennera.jgettext.Message;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class AbstractExtractorHandler extends NestedContentHandler implements Locator, NamespaceContext {

    private final NamespaceContextImpl namespaceContext;
    private final MessageBundle bundle;
    private Locator locator;
    private boolean relativizePaths;
    File rootDir;

    public AbstractExtractorHandler(XMLReader xmlreader, MessageBundle bundle) {
        this(xmlreader, bundle, null, false);
    }

    public AbstractExtractorHandler(XMLReader xmlreader, MessageBundle bundle, File rootDir, boolean relativizePaths) {
        super(xmlreader);
        this.bundle = bundle;
        this.namespaceContext = new NamespaceContextImpl();
        this.relativizePaths = relativizePaths;
        this.rootDir = rootDir;
    }

    private Message createMessage(String msgid) {
        Message msg = new Message();
        if (locator != null) {
            String systemId = locator.getSystemId();
            if (systemId != null) {
                if (relativizePaths && rootDir != null) {
                    try {
                        systemId = URLDecoder.decode(systemId, "UTF-8");
                    } catch (Exception e) {
                    }

                    Pattern ptnProtocol = Pattern.compile("([a-zA-Z]+://)");
                    Matcher m = ptnProtocol.matcher(systemId);
                    String protocol = "";
                    if (m.find()) {
                        // protocol = m.group(m.groupCount());
                        protocol = m.group(0);
                    }

                    int idx = StringUtils.isEmpty(protocol) ? -1 : systemId.lastIndexOf(protocol);
                    String cleanRef = (idx != -1 ? systemId.substring(idx + protocol.length()) : systemId).replace(protocol, "");
                    /*
                     * if (cleanRef.startsWith("/")) cleanRef = cleanRef.substring(1);
                     */
                    // extract filename and filepath from cleanref
                    final String fileName = FilenameUtils.getName(cleanRef);
                    String filePath = FilenameUtils.getFullPath(cleanRef);
                    if (filePath.startsWith("/") && filePath.contains(":")) // (absolute) DOS-Path with starting "/" ???
                        filePath = filePath.substring(1);
                    systemId = ResourceUtils.getRelativePath(filePath + fileName, FilenameUtils.separatorsToUnix(rootDir.getAbsolutePath()), "/");
                }
                int line = locator.getLineNumber();
                if (line > 0) {
                    msg.addSourceReference(systemId, line);
                } else {
                    msg.addSourceReference(systemId);
                }
            }
        }
        msg.setMsgid(msgid);
        // msg.setFuzzy(true);
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
