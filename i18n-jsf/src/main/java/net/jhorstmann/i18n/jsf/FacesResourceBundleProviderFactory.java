package net.jhorstmann.i18n.jsf;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

import net.jhorstmann.i18n.ResourceBundleProvider;
import net.jhorstmann.i18n.ResourceBundleProviderFactory;

public class FacesResourceBundleProviderFactory extends ResourceBundleProviderFactory {
    public static final String KEY_DEACTIVATION_VAR = "net.jhorstmann.i18n.FacesResourceBundleProviderFactory.deactivate";

    static class FacesResourceBundleProvider implements ResourceBundleProvider {

        @Override
        public ResourceBundle getResourceBundle(Locale locale) {
            return FacesResourceBundle.getResourceBundle();
        }
    }

    private static final ResourceBundleProvider PROVIDER = new FacesResourceBundleProvider();

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
    public ResourceBundleProvider newResourceBundleProvider() {
        return PROVIDER;
    }
}
