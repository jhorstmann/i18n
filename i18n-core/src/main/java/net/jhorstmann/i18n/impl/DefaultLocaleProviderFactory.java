package net.jhorstmann.i18n.impl;

import java.util.Locale;
import net.jhorstmann.i18n.LocaleProvider;
import net.jhorstmann.i18n.LocaleProviderFactory;

/**
 * {@Link LocaleProviderFactory} implementation using {@link java.util.Locale#getDefault()}.
 * @author Jörn Horstmann
 */
public class DefaultLocaleProviderFactory extends LocaleProviderFactory {

    static class DefaultLocaleProvider implements LocaleProvider {

        @Override
        public Locale getLocale() {
            return Locale.getDefault();
        }
    }
    
    private static final LocaleProvider PROVIDER = new DefaultLocaleProvider();

    @Override
    public boolean isEnvironmentSupported() {
        return true;
    }
    
    @Override
    public LocaleProvider newLocaleProvider() {
        return PROVIDER;
    }
}
