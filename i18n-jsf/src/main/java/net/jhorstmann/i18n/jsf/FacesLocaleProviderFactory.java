package net.jhorstmann.i18n.jsf;

import java.util.Locale;
import javax.faces.context.FacesContext;

import net.jhorstmann.i18n.LocaleProvider;
import net.jhorstmann.i18n.LocaleProviderFactory;

public class FacesLocaleProviderFactory extends LocaleProviderFactory {

    static class JsfLocaleProvider implements LocaleProvider {

        @Override
        public Locale getLocale() {
            return FacesLocale.getLocale();
        }
    }
    private static final LocaleProvider PROVIDER = new JsfLocaleProvider();

    @Override
    public boolean isEnvironmentSupported() {
        return FacesContext.getCurrentInstance() != null;
    }

    @Override
    public LocaleProvider newLocaleProvider() {
        return PROVIDER;
    }
}
