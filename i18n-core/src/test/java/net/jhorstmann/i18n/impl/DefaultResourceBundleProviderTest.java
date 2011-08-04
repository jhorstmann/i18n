package net.jhorstmann.i18n.impl;

import java.util.Locale;
import java.util.ResourceBundle;
import net.jhorstmann.i18n.ResourceBundleProvider;
import org.junit.Assert;
import org.junit.Test;

public class DefaultResourceBundleProviderTest {

    @Test
    public void loadBundleName() {
        ClassLoader cl = DefaultResourceBundleProviderTest.class.getClassLoader();
        String bundleName = DefaultResourceBundle.loadBundleName(cl);
        Assert.assertEquals("net.jhorstmann.i18n.Messages", bundleName);
    }
    
    @Test
    public void testMessage() {
        ResourceBundleProvider provider = DefaultResourceBundleProviderFactory.newInstance().newResourceBundleProvider();
        ResourceBundle bundleEN = provider.getResourceBundle(Locale.ENGLISH);
        Assert.assertNotNull(bundleEN);
        Assert.assertEquals(Locale.ENGLISH, bundleEN.getLocale());
        Assert.assertTrue(bundleEN.containsKey("hello"));
        Assert.assertEquals("Hello World", bundleEN.getString("hello"));

        ResourceBundle bundleDE = provider.getResourceBundle(Locale.GERMANY);
        Assert.assertNotNull(bundleDE);
        Assert.assertEquals(Locale.GERMAN, bundleDE.getLocale());
        Assert.assertTrue(bundleDE.containsKey("hello"));
        Assert.assertEquals("Hallo Welt", bundleDE.getString("hello"));
    }
}
