package net.jhorstmann.i18n;

import java.util.Locale;
import java.util.ResourceBundle;
import org.junit.Assert;
import org.junit.Test;

public class I18NTest {

    @Test
    public void testGetLocale() {
        Locale.setDefault(Locale.GERMANY);
        Assert.assertEquals(Locale.GERMANY, I18N.getLocale());
        Locale.setDefault(Locale.JAPAN);
        Assert.assertEquals(Locale.JAPAN, I18N.getLocale());
    }

    @Test
    public void testGetBundle() {
        ResourceBundle bundle = I18N.getBundle(Locale.GERMANY);
        Assert.assertNotNull(bundle);
        Assert.assertTrue(bundle.containsKey("hello"));
        Assert.assertEquals(Locale.GERMAN,  bundle.getLocale());
        Assert.assertEquals("Hallo Welt", bundle.getString("hello"));
    }
    
    @Test
    public void testMessage() {
        Locale.setDefault(Locale.GERMANY);
        Assert.assertEquals("Hallo Welt", I18N.tr("hello"));
        Locale.setDefault(Locale.ENGLISH);
        Assert.assertEquals("Hello World", I18N.tr("hello"));
        Locale.setDefault(Locale.FRANCE);
        Assert.assertEquals("Hello World (default)", I18N.tr("hello"));
    }

    @Test
    public void testWithParameter() {
        Locale.setDefault(Locale.GERMANY);
        Assert.assertEquals("Hallo Test", I18N.tr("helloParam", "Test"));
    }

    @Test
    public void testWithArrayParameter() {
        Locale.setDefault(Locale.GERMANY);
        Assert.assertEquals("Hallo Test", I18N.tr("helloParam", new Object[]{"Test"}));
    }

    @Test
    public void testQuotes() {
        Locale.setDefault(Locale.GERMANY);
        Assert.assertEquals("Hallo 'Anführungszeichen'", I18N.tr("helloQuotes"));
    }

    @Test
    public void testQuotesAndParam() {
        Locale.setDefault(Locale.GERMANY);
        Assert.assertEquals("Hallo 'Test'", I18N.tr("helloQuotesParam", "Test"));
    }
}
