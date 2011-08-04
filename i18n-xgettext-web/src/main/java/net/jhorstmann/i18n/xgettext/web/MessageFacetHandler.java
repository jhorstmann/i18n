package net.jhorstmann.i18n.xgettext.web;

import org.xml.sax.Attributes;

public class MessageFacetHandler extends FacetHandler {
    public MessageFacetHandler(ExtractorHandler extractor, ComponentHandler component, Attributes atts) {
        super(extractor, component, atts);
    }

    @Override
    public void endElement(String content) {
        getComponent().setMessage(content);
    }
}
