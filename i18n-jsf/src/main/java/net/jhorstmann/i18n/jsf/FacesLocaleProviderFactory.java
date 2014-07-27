package net.jhorstmann.i18n.jsf;

import java.util.Locale;

import javax.faces.context.FacesContext;

import net.jhorstmann.i18n.LocaleProvider;
import net.jhorstmann.i18n.LocaleProviderFactory;

public class FacesLocaleProviderFactory extends LocaleProviderFactory {

    public static final String KEY_DEACTIVATION_VAR = "net.jhorstmann.i18n.FacesLocaleProviderFactory.deactivate";

    static class JsfLocaleProvider implements LocaleProvider {

        @Override
        public Locale getLocale() {
            return FacesLocale.getLocale();
        }
    }

    private static final LocaleProvider PROVIDER = new JsfLocaleProvider();

    /**
     * If the initial-param "net.jhorstmann.i18n.FacesLocaleProviderFactory.deactivate" is set to true, this Provider is deactivated, in all other cases it
     * returns the value of (FacesContext.getCurrentInstance() != null)
     */
    @Override
    public boolean isEnvironmentSupported() {
        if (FacesContext.getCurrentInstance() == null) { // no JSF active
            return false;
        }
        String bundleVar = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(KEY_DEACTIVATION_VAR);
        if (bundleVar != null && "true".equalsIgnoreCase(bundleVar)) { // Provider is disabled
            return false;
        }
        // JSF is active and no deactivation configured
        return true;
    }

    @Override
    public LocaleProvider newLocaleProvider() {
        return PROVIDER;
    }
}
