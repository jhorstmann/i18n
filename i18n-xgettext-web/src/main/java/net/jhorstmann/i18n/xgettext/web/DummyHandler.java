package net.jhorstmann.i18n.xgettext.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DummyHandler extends DefaultContentHandler {

    private static final Logger log = LoggerFactory.getLogger(DummyHandler.class);
    private ExtractorHandler extractor;
    private int depth;

    public DummyHandler(ExtractorHandler extractor) {
        this.extractor = extractor;
        this.depth = 0;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        log.debug("Ignoring start tag {}", localName);
        depth++;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        log.debug("Ignoring end tag {}", localName);
        depth--;
        if (depth < 0) {
            log.debug("Restoring parent handler");
            extractor.popHandler();
        }
    }
}
