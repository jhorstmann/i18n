package net.jhorstmann.i18n.xgettext.web;

import org.xml.sax.Attributes;

public class ContextFacetHandler extends FacetHandler {
    public ContextFacetHandler(WebMessageExtractor extractor, ComponentHandler component, Attributes atts) {
        super(extractor, component, atts);
    }

    @Override
    public void endElement(String content) {
        getComponent().setContext(content);
    }
}
