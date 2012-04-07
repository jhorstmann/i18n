package net.jhorstmann.i18n.xgettext.web;

import net.jhorstmann.i18n.tools.xml.DefaultContentHandler;
import javax.xml.XMLConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ComponentHandler extends DefaultContentHandler implements Constants {
    private Logger log = LoggerFactory.getLogger(ComponentHandler.class);

    private final WebMessageExtractor extractor;
    private final StringBuilder text;
    private String message;
    private String context;
    private String plural;
    private String comment;

    public ComponentHandler(WebMessageExtractor extractor, Attributes atts) {
        this.extractor = extractor;
        this.text = new StringBuilder();
        this.message = atts.getValue(XMLConstants.NULL_NS_URI, ATTR_MSGID);
        this.context = atts.getValue(XMLConstants.NULL_NS_URI, ATTR_MSGCTX);
        this.plural = atts.getValue(XMLConstants.NULL_NS_URI, ATTR_MSGPLURAL);
        this.comment = atts.getValue(XMLConstants.NULL_NS_URI, ATTR_MSGCOMMENT);
    }
    
    @Override
    public void setDocumentLocator(Locator locator) {
        extractor.setDocumentLocator(locator);
    }

    private ContentHandler createFacetHandler(String name, Attributes atts) throws SAXException {
        if ("context".equals(name)) {
            return new ContextFacetHandler(extractor, this, atts);
        } else if ("message".equals(name)) {
            return new MessageFacetHandler(extractor, this, atts);
        } else if ("plural".equals(name)) {
            return new PluralFacetHandler(extractor, this, atts);
        } else if ("comment".equals(name)) {
            return new CommentFacetHandler(extractor, this, atts);
        } else {
            log.warn("Unknown facet name '{}' in translation component", name);
            return new DummyHandler(extractor);
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        if (NS_JSF_CORE.equals(uri) && "facet".equals(localName)) {
            String name = atts.getValue(XMLConstants.NULL_NS_URI, "name");
            extractor.pushHandler(createFacetHandler(name, atts));
        } else if (NS_JSF_CORE.equals(uri) && "param".equals(localName)) {
            extractor.pushHandler(new DummyHandler(extractor));
        } else if (NS_I18N_COMPONENT.equals(uri)) {
            extractor.pushHandler(createFacetHandler(localName, atts));
        } else {
            log.warn("Unexpected element '{{}}{}' in translation component", new Object[]{uri, localName});
            extractor.pushHandler(new DummyHandler(extractor));
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        extractor.popHandler();
        String content = text.toString().trim();
        String msgid = content != null && content.length() > 0 ? content : message;
        extractor.addMessage(context, msgid, plural, comment);
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        text.append(ch, start, length);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getPlural() {
        return plural;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
