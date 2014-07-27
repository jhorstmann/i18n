package net.jhorstmann.i18n.xgettext.struts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.jhorstmann.i18n.tools.xml.DefaultContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StrutsHtmlHandler extends DefaultContentHandler {

    private static final String NAMESPACE_JAKARTA_HTML = "http://jakarta.apache.org/struts/tags-html";
    private static final String NAMESPACE_STRUTS_HTML  = "http://struts.apache.org/struts/tags-html";
    private static final String NAMESPACE_JAKARTA_BEAN = "http://jakarta.apache.org/struts/tags-bean";
    private static final String NAMESPACE_STRUTS_BEAN  = "http://struts.apache.org/struts/tags-bean";
    private static final String NAMESPACE_WEBINF_HTML  = "/WEB-INF/struts-html.tld";
    private static final String NAMESPACE_WEBINF_BEAN  = "/WEB-INF/struts-bean.tld";
    private static final String NAMESPACE_JSTL_FMT     = "http://java.sun.com/jstl/fmt";

    private static final HashMap<String, Map<String, List<String>>> ATTRS = new HashMap<String, Map<String, List<String>>>();

    static {
        HashMap<String, List<String>> html = new HashMap<String, List<String>>();
        HashMap<String, List<String>> bean = new HashMap<String, List<String>>();
        HashMap<String, List<String>> fmt = new HashMap<String, List<String>>();

        List<String> key = Arrays.asList("key");
        List<String> title = Arrays.asList("titleKey");
        List<String> alttitle = Arrays.asList("altKey", "titleKey");
        List<String> srcalttitle = Arrays.asList("srcKey", "altKey", "titleKey");

        html.put("button", alttitle);
        html.put("cancel", alttitle);
        html.put("checkbox", alttitle);
        html.put("file", alttitle);
        html.put("frame", title);
        html.put("hidden", alttitle);
        html.put("image", srcalttitle);
        html.put("img", srcalttitle);
        html.put("link", title);
        html.put("multibox", alttitle);
        html.put("option", key);
        html.put("password", alttitle);
        html.put("radio", alttitle);
        html.put("reset", alttitle);
        html.put("select", alttitle);
        html.put("submit", alttitle);
        html.put("text", alttitle);
        html.put("textarea", alttitle);
        html.put("messages", Arrays.asList("footer", "header"));
        html.put("errors", Arrays.asList("footer", "header", "prefix", "suffix"));

        bean.put("message", key);

        fmt.put("message", key);

        ATTRS.put(NAMESPACE_STRUTS_HTML, html);
        ATTRS.put(NAMESPACE_JAKARTA_HTML, html);
        ATTRS.put(NAMESPACE_WEBINF_HTML, html);

        ATTRS.put(NAMESPACE_STRUTS_BEAN, bean);
        ATTRS.put(NAMESPACE_JAKARTA_BEAN, bean);
        ATTRS.put(NAMESPACE_WEBINF_BEAN, bean);

        ATTRS.put(NAMESPACE_JSTL_FMT, fmt);
    }

    private StrutsHtmlMessageExtractor extractor;

    public StrutsHtmlHandler(StrutsHtmlMessageExtractor extractor) {
        this.extractor = extractor;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
        Map<String, List<String>> map = ATTRS.get(uri);
        if (map != null) {
            List<String> keys = map.get(localName);
            if (keys != null) {
                for (String key : keys) {
                    String value = atts.getValue(key);
                    if (value != null) {
                        extractor.addMessage(value);
                    }
                }
            }
        }

        super.startElement(uri, localName, qName, atts);
    }
}
