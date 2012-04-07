package net.jhorstmann.i18n.xgettext.web;

import net.jhorstmann.i18n.tools.xml.DefaultContentHandler;
import javax.el.ELException;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.apache.el.parser.ELParser;
import org.apache.el.parser.Node;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RootHandler extends DefaultContentHandler {

    private static final String I18N_NS = "http://jhorstmann.net/taglib/i18n";
    private WebMessageExtractor extractor;
    private StringBuilder buffer;

    public RootHandler(WebMessageExtractor extractor) {
        this.extractor = extractor;
        this.buffer = new StringBuilder();
    }

    private void handleText(String str) throws ELException, SAXException {
        if (str.length() > 0) {
            Node node = ELParser.parse(str);
            try {
                node.accept(new GettextNodeVisitor(extractor));
            } catch (Exception ex) {
                if (ex instanceof RuntimeException) {
                    throw (RuntimeException)ex;
                } else {
                    throw new SAXException("Exception from node visitor", ex);
                }
            }
        }
    }

    private void clearBuffer() {
        buffer.setLength(0);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        extractor.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        extractor.endPrefixMapping(prefix);
    }

    private void handleAttributes(Attributes atts) throws SAXException {
        for (int i = 0, len = atts.getLength(); i < len; i++) {
            String value = atts.getValue(i);
            handleText(value);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        handleText(buffer.toString());
        clearBuffer();
        if (I18N_NS.equals(uri) && "tr".equals(localName)) {
            extractor.pushHandler(new ComponentHandler(extractor, atts));
        } else {
            String jsfc = atts.getValue(XMLConstants.NULL_NS_URI, "jsfc");
            if (jsfc != null) {
                QName component = extractor.resolveQName(jsfc);
                if (XMLConstants.NULL_NS_URI.equals(component.getNamespaceURI()) && "tr".equals(component.getLocalPart())) {
                    extractor.pushHandler(new ComponentHandler(extractor, atts));
                } else {
                    handleAttributes(atts);
                }
            } else {
                handleAttributes(atts);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        handleText(buffer.toString());
        clearBuffer();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        buffer.append(ch, start, length);
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        handleText(new String(ch, start, length));
    }
}
