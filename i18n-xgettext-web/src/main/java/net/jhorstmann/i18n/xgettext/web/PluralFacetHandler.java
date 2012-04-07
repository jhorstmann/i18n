package net.jhorstmann.i18n.xgettext.web;

import org.xml.sax.Attributes;

public class PluralFacetHandler extends FacetHandler {
    public PluralFacetHandler(WebMessageExtractor extractor, ComponentHandler component, Attributes atts) {
        super(extractor, component, atts);
    }

    @Override
    public void endElement(String content) {
        getComponent().setPlural(content);
    }
}
