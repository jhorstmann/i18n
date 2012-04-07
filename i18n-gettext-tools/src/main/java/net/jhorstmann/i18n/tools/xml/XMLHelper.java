package net.jhorstmann.i18n.tools.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

public class XMLHelper {

    private XMLHelper() {
    }

    public static SAXParserFactory createParserFactory() {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        try {
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (ParserConfigurationException ex) {
        } catch (SAXNotRecognizedException ex) {
        } catch (SAXNotSupportedException ex) {
        }
        try {
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException ex) {
        } catch (SAXNotRecognizedException ex) {
        } catch (SAXNotSupportedException ex) {
        }
        return factory;
    }

    public static XMLReader createXMLReader() {
        SAXParserFactory factory = createParserFactory();
        try {
            SAXParser parser = factory.newSAXParser();
            return parser.getXMLReader();
        } catch (ParserConfigurationException ex) {
            throw new IllegalStateException(ex);
        } catch (SAXException ex) {
            throw new IllegalStateException(ex);
        }
    }
}
