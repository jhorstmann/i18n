package net.jhorstmann.i18n.xgettext.web;

import net.jhorstmann.i18n.tools.xml.DefaultContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class FacetHandler extends DefaultContentHandler {

    private final WebMessageExtractor extractor;
    private final ComponentHandler component;
    private final StringBuilder text;

    public FacetHandler(WebMessageExtractor extractor, ComponentHandler component, Attributes atts) {
        this.extractor = extractor;
        this.component = component;
        this.text = new StringBuilder();
    }

    public ComponentHandler getComponent() {
        return component;
    }

    public abstract void endElement(String content);

    @Override
    public final void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        throw new SAXException("Unexpected element content in facet");
    }

    @Override
    public final void endElement(String uri, String localName, String qName) throws SAXException {
        extractor.popHandler();
        endElement(text.toString());
    }

    @Override
    public final void characters(char[] ch, int start, int length) throws SAXException {
        text.append(ch, start, length);
    }
}
