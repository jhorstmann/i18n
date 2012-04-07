package net.jhorstmann.i18n.xgettext.web;

import net.jhorstmann.i18n.tools.xml.NamespaceContextImpl;
import java.util.Iterator;
import junit.framework.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

public class NamespaceContextTest {

    @Test
    public void testSimple() throws SAXException {
        NamespaceContextImpl nc = new NamespaceContextImpl();
        
        nc.startPrefixMapping("a", "urn:a");
        Assert.assertEquals("urn:a", nc.getNamespaceURI("a"));
        Assert.assertEquals("a", nc.getPrefix("urn:a"));
        Iterator prefixes = nc.getPrefixes("urn:a");
        Assert.assertNotNull(prefixes);
        Assert.assertTrue(prefixes.hasNext());
        Assert.assertEquals("a", prefixes.next());
        Assert.assertFalse(prefixes.hasNext());
        nc.endPrefixMapping("a");
    }

    @Test
    public void testNested() throws SAXException {
        NamespaceContextImpl nc = new NamespaceContextImpl();
        
        nc.startPrefixMapping("a", "urn:a");
        nc.startPrefixMapping("a", "urn:a2");
        Assert.assertEquals("urn:a2", nc.getNamespaceURI("a"));
        Assert.assertEquals("a", nc.getPrefix("urn:a2"));
        Iterator prefixes = nc.getPrefixes("urn:a2");
        Assert.assertNotNull(prefixes);
        Assert.assertTrue(prefixes.hasNext());
        Assert.assertEquals("a", prefixes.next());
        Assert.assertFalse(prefixes.hasNext());
        nc.endPrefixMapping("a");
        nc.endPrefixMapping("a");
    }

}
