package net.jhorstmann.i18n.xgettext.web;

import org.xml.sax.Attributes;

public class CommentFacetHandler extends FacetHandler {
    public CommentFacetHandler(ExtractorHandler extractor, ComponentHandler component, Attributes atts) {
        super(extractor, component, atts);
    }

    @Override
    public void endElement(String content) {
        getComponent().setComment(content);
    }
}
