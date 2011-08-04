package net.jhorstmann.i18n.jsf;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import net.jhorstmann.i18n.ResourceBundleProvider;
import net.jhorstmann.i18n.ResourceBundleProviderFactory;

public class FacesResourceBundleProviderFactory extends ResourceBundleProviderFactory {
    
    static class FacesResourceBundleProvider implements ResourceBundleProvider {

        @Override
        public ResourceBundle getResourceBundle(Locale locale) {
            return FacesResourceBundle.getResourceBundle();
        }
    }
    
    private static final ResourceBundleProvider PROVIDER = new FacesResourceBundleProvider();

    @Override
    public boolean isEnvironmentSupported() {
        return FacesContext.getCurrentInstance() != null;
    }

    @Override
    public ResourceBundleProvider newResourceBundleProvider() {
        return PROVIDER;
    }
}
