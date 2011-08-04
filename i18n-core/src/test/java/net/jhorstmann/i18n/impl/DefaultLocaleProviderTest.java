package net.jhorstmann.i18n.impl;

import java.util.Iterator;
import java.util.Locale;
import java.util.ServiceLoader;
import net.jhorstmann.i18n.I18N;
import net.jhorstmann.i18n.LocaleProvider;
import net.jhorstmann.i18n.LocaleProviderFactory;
import net.jhorstmann.i18n.impl.DefaultLocaleProviderFactory;
import org.junit.Assert;
import org.junit.Test;

public class DefaultLocaleProviderTest {

    @Test
    public void testServiceLoader() {
        ServiceLoader<LocaleProviderFactory> loader = ServiceLoader.load(LocaleProviderFactory.class);
        Iterator<LocaleProviderFactory> iterator = loader.iterator();
        Assert.assertTrue(iterator.hasNext());
        LocaleProviderFactory factory = iterator.next();
        Assert.assertTrue(factory instanceof DefaultLocaleProviderFactory);
        LocaleProvider provider = factory.newLocaleProvider();
        Assert.assertNotNull(provider);
    }

    @Test
    public void testFactory() {
        LocaleProvider provider = LocaleProviderFactory.newInstance().newLocaleProvider();
        Assert.assertNotNull(provider);
    }
    
    @Test
    public void testProvider() {
        LocaleProvider provider = LocaleProviderFactory.newInstance().newLocaleProvider();
        Assert.assertNotNull(provider);
        Locale.setDefault(Locale.GERMANY);
        Assert.assertEquals(Locale.GERMANY, provider.getLocale());
        Locale.setDefault(Locale.FRANCE);
        Assert.assertEquals(Locale.FRANCE, provider.getLocale());
    }
    
}
