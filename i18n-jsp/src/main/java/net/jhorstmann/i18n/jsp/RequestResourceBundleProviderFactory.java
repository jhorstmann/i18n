package net.jhorstmann.i18n.jsp;

import net.jhorstmann.i18n.ResourceBundleProvider;
import net.jhorstmann.i18n.ResourceBundleProviderFactory;

public class RequestResourceBundleProviderFactory extends ResourceBundleProviderFactory {
    
    private static final ResourceBundleProvider PROVIDER = new RequestResourceBundleProvider();

    @Override
    public boolean isEnvironmentSupported() {
        return I18nFilter.getCurrentRequest() != null;
    }

    @Override
    public ResourceBundleProvider newResourceBundleProvider() {
        return PROVIDER;
    }
}
