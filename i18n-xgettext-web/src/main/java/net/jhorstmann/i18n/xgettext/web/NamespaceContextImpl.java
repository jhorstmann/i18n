package net.jhorstmann.i18n.xgettext.web;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;

import org.xml.sax.SAXException;

public final class NamespaceContextImpl implements NamespaceContext {
    static class PrefixMapping {

        String prefix;
        String uri;

        PrefixMapping(String prefix, String uri) {
            this.prefix = prefix;
            this.uri = uri;
        }
    }

    private final LinkedList<PrefixMapping> namespaces = new LinkedList<PrefixMapping>();

    public final void startPrefixMapping(String prefix, String uri) throws SAXException {
        namespaces.addFirst(new PrefixMapping(prefix, uri));
    }

    public final void endPrefixMapping(String prefix) throws SAXException {
        for (Iterator<PrefixMapping> it=namespaces.iterator(); it.hasNext(); ) {
            PrefixMapping mapping = it.next();
            if (prefix.equals(mapping.prefix)) {
                it.remove();
                return;
            }
        }
        throw new SAXException("End prefix mapping for unknown prefix '" + prefix + "'");
    }

    @Override
    public final String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Prefix must not be null");
        } else if (prefix.equals(XMLConstants.XML_NS_PREFIX)) {
            return XMLConstants.XML_NS_URI;
        } else if (prefix.equals(XMLConstants.XMLNS_ATTRIBUTE)) {
            return XMLConstants.XMLNS_ATTRIBUTE_NS_URI;
        } else {
            for (PrefixMapping mapping : namespaces) {
                if (prefix.equals(mapping.prefix)) {
                    return mapping.uri;
                }
            }
            return XMLConstants.NULL_NS_URI;
        }
    }

    @Override
    public final String getPrefix(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("URI must not be null");
        } else if (uri.equals(XMLConstants.XML_NS_URI)) {
            return XMLConstants.XML_NS_PREFIX;
        } else if (uri.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return XMLConstants.XMLNS_ATTRIBUTE;
        } else {
            for (PrefixMapping mapping : namespaces) {
                if (uri.equals(mapping.uri)) {
                    return mapping.prefix;
                }
            }
            return null;
        }
    }

    @Override
    public final Iterator getPrefixes(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("URI must not be null");
        } else if (uri.equals(XMLConstants.XML_NS_URI)) {
            return Collections.singleton(XMLConstants.XML_NS_PREFIX).iterator();
        } else if (uri.equals(XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            return Collections.singleton(XMLConstants.XMLNS_ATTRIBUTE).iterator();
        } else {
            LinkedList<String> res = new LinkedList<String>();
            for (PrefixMapping mapping : namespaces) {
                if (uri.equals(mapping.uri)) {
                    res.add(mapping.prefix);
                }
            }
            return Collections.unmodifiableCollection(res).iterator();
        }
    }

    public final QName resolveQName(String name) {
        int idx = name.indexOf(':');
        if (idx == 0) {
            throw new IllegalArgumentException("Name must not start with a colon");
        } else if (idx < 0) {
            return new QName(XMLConstants.NULL_NS_URI, name);
        } else {
            String prefix = name.substring(0, idx);
            String localName = name.substring(idx+1);
            String uri = getNamespaceURI(prefix);
            return new QName(uri, localName, prefix);
        }
    }
}
