package net.jhorstmann.i18n.impl;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import net.jhorstmann.i18n.ResourceBundleProvider;
import net.jhorstmann.i18n.ResourceBundleProviderFactory;

public class DefaultResourceBundleProviderFactory extends ResourceBundleProviderFactory {
    static class DefaultResourceBundleProvider implements ResourceBundleProvider {
        private final String bundleName;

        DefaultResourceBundleProvider(String bundleName) {
            this.bundleName = bundleName;
        }
        
        @Override
        public ResourceBundle getResourceBundle(Locale locale) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            Control control = Control.getNoFallbackControl(Control.FORMAT_DEFAULT);
            return ResourceBundle.getBundle(bundleName, locale, cl, control);
        }
    }
    
    private final ResourceBundleProvider provider;
    
    public DefaultResourceBundleProviderFactory() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        String bundleName = DefaultResourceBundle.getBundleName(cl);
        this.provider = new DefaultResourceBundleProvider(bundleName);
    }

    @Override
    public boolean isEnvironmentSupported() {
        return true;
    }

    @Override
    public ResourceBundleProvider newResourceBundleProvider() {
        return provider;
    }
}
