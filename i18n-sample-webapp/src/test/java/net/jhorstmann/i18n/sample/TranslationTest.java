package net.jhorstmann.i18n.sample;

import java.util.Locale;
import net.jhorstmann.i18n.I18N;
import org.junit.Assert;
import org.junit.Test;

/**
 * Translation works in Tests by configuring the neccessary Providers in src/test/resources.
 */
public class TranslationTest {
    @Test
    public void testGerman() {
        Locale.setDefault(Locale.GERMANY);
        Assert.assertEquals("Einfache Nachricht", I18N.tr("Simple message"));
        Assert.assertEquals("Nachricht mit Kontext", I18N.trc("Message with context", "Message with context"));
    }

    @Test
    public void testEnglish() {
        Locale.setDefault(Locale.ENGLISH);
        Assert.assertEquals("Simple message", I18N.tr("Simple message"));
        Assert.assertEquals("Message with context", I18N.trc("Message with context", "Message with context"));
    }
}
