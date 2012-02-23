package net.jhorstmann.i18n.jsp;

import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import net.jhorstmann.i18n.ResourceBundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestResourceBundleProvider implements ResourceBundleProvider {

    private static final Logger log = LoggerFactory.getLogger(RequestResourceBundleProvider.class);

    static ResourceBundle loadBundle(String bundleName, Locale locale) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ResourceBundle.Control control = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);
        log.debug("Loading ResourceBundle {} for locale {}", bundleName, locale);
        return ResourceBundle.getBundle(bundleName, locale, cl, control);
    }

    public static ResourceBundle getResourceBundle(HttpServletRequest req, Locale locale) {
        ResourceBundle bundle = (ResourceBundle) req.getAttribute(I18nFilter.KEY_RESOURCE_BUNDLE);
        if (bundle == null) {
            String bundleName = (String) req.getAttribute(I18nFilter.KEY_RESOURCE_BUNDLE_NAME);
            if (bundleName != null) {
                bundle = loadBundle(bundleName, locale);
                req.setAttribute(I18nFilter.KEY_RESOURCE_BUNDLE, bundle);
            } else {
                log.warn("No ResourceBundle name configured");
            }
        }
        return bundle;
    }

    @Override
    public ResourceBundle getResourceBundle(Locale locale) {
        HttpServletRequest currentRequest = I18nFilter.getCurrentRequest();
        return getResourceBundle(currentRequest, locale);
    }
}
