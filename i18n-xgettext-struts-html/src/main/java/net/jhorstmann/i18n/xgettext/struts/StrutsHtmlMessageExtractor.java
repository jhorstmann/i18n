package net.jhorstmann.i18n.xgettext.struts;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.el.ELException;
import net.jhorstmann.i18n.tools.MessageBundle;
import net.jhorstmann.i18n.tools.xgettext.AbstractExtractorHandler;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractor;
import net.jhorstmann.i18n.tools.xgettext.MessageExtractorException;
import net.jhorstmann.i18n.tools.xml.XMLHelper;
import net.jhorstmann.jspparser.StreamingParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class StrutsHtmlMessageExtractor extends AbstractExtractorHandler implements MessageExtractor{
    private static final Logger log = LoggerFactory.getLogger(StrutsHtmlMessageExtractor.class);

    public StrutsHtmlMessageExtractor(MessageBundle bundle, boolean isXML) {
        super(isXML ? XMLHelper.createXMLReader() : createJSPReader(), bundle);
    }

    private static XMLReader createJSPReader() {
        return new StreamingParser();
    }
    
    @Override
    public void startDocument() throws SAXException {
        pushHandler(new StrutsHtmlHandler(this));
    }

    @Override
    public void endDocument() throws SAXException {
        popHandler();
    }

    @Override
    public void extractMessages(File file) throws IOException, MessageExtractorException {
        String systemId = file.getAbsolutePath();
        log.debug("Extracting messages from {}", systemId);
        InputStream in = new FileInputStream(file);
        try {
            InputSource input = new InputSource(in);
            input.setSystemId(systemId);
            extractMessages(input);
        } finally {
            in.close();
        }
    }

    void extractMessages(InputSource input) throws IOException, MessageExtractorException {
        XMLReader xmlreader = getXMLReader();
        xmlreader.setContentHandler(this);
        try {
            xmlreader.parse(input);
        } catch (SAXException ex) {
            throw new MessageExtractorException(ex);
        } catch (ELException ex) {
            throw new MessageExtractorException(ex);
        }
    }
}
