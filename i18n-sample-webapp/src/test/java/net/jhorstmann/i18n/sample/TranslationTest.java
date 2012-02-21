package net.jhorstmann.i18n.sample;

import java.util.Locale;
import net.jhorstmann.i18n.I18N;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Translation works in Tests by configuring the neccessary Providers in src/test/resources.
 */
public class TranslationTest {
    private Locale locale;
    @Before
    public void saveLocale() {
        locale = Locale.getDefault();
    }

    @After
    public void restoreLocale() {
        Locale.setDefault(locale);
    }

    @Test
    public void testGerman() {
        Locale.setDefault(Locale.GERMANY);
        Assert.assertEquals("Einfache Nachricht", I18N.tr("Simple message"));
        Assert.assertEquals("Nachricht mit Kontext", I18N.trc("Message with context", "Message with context"));
    }

    @Test
    public void testGermanPlural() {
        Locale.setDefault(Locale.GERMANY);
        Assert.assertEquals("Mehrere Nachrichten", I18N.trn("One Message", "Several Messages", 0));
        Assert.assertEquals("Eine Nachricht", I18N.trn("One Message", "Several Messages", 1));
        Assert.assertEquals("Mehrere Nachrichten", I18N.trn("One Message", "Several Messages", 2));
    }

    @Test
    public void testEnglish() {
        Locale.setDefault(Locale.ENGLISH);
        Assert.assertEquals("Simple message", I18N.tr("Simple message"));
        Assert.assertEquals("Message with context", I18N.trc("Message with context", "Message with context"));
    }

    @Test
    public void testEnglishPlural() {
        Locale.setDefault(Locale.ENGLISH);
        Assert.assertEquals("Several Messages", I18N.trn("One Message", "Several Messages", 0));
        Assert.assertEquals("One Message", I18N.trn("One Message", "Several Messages", 1));
        Assert.assertEquals("Several Messages", I18N.trn("One Message", "Several Messages", 2));
    }

    @Test
    public void testDefault() {
        Locale.setDefault(Locale.CHINESE);
        Assert.assertEquals("Simple message", I18N.tr("Simple message"));
        Assert.assertEquals("Message with context", I18N.trc("Message with context", "Message with context"));
    }

    @Test
    public void testDefaultPlural() {
        Locale.setDefault(Locale.CHINESE);
        Assert.assertEquals("Several Messages", I18N.trn("One Message", "Several Messages", 0));
        Assert.assertEquals("One Message", I18N.trn("One Message", "Several Messages", 1));
        Assert.assertEquals("Several Messages", I18N.trn("One Message", "Several Messages", 2));
    }

}
