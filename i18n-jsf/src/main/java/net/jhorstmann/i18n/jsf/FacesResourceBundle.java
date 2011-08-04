package net.jhorstmann.i18n.jsf;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import javax.faces.context.FacesContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tries to look up a ResourceBundle with the basename configured using
 * the {@value #KEY_RESOURCE_BUNDLE} init parameter in web.xml. If that
 * fails it uses the value of the {@value #KEY_RESOURCE_BUNDLE_VAR} init
 * parameter to get a ResourceBundle using
 * {@link javax.faces.application.Application#getResourceBundle(javax.faces.context.FacesContext, java.lang.String)}.
 * 
 * @author Jörn Horstmann
 */
public class FacesResourceBundle {
    private static final Logger log = LoggerFactory.getLogger(FacesResourceBundle.class);

    public static final String DEFAULT_RESOURCE_BUNDLE_VAR = "i18n";
    public static final String KEY_RESOURCE_BUNDLE = "net.jhorstmann.i18n.ResourceBundle";
    public static final String KEY_RESOURCE_BUNDLE_VAR = "net.jhorstmann.i18n.ResourceBundleVar";

    private FacesResourceBundle() {

    }

    private static ResourceBundle getResourceBundleImpl(FacesContext ctx, Locale locale) {
        String bundleName = ctx.getExternalContext().getInitParameter(KEY_RESOURCE_BUNDLE);
        if (bundleName != null) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            ResourceBundle.Control control = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);
            log.debug("Loading ResourceBundle {} for locale {}", bundleName, locale);
            return ResourceBundle.getBundle(bundleName, locale, cl, control);
        } else {
            String bundleVar = ctx.getExternalContext().getInitParameter(KEY_RESOURCE_BUNDLE_VAR);
            if (bundleVar == null) {
                bundleVar = DEFAULT_RESOURCE_BUNDLE_VAR;
            }
            log.debug("Loading ResourceBundle {} from Application", bundleVar);
            ResourceBundle bundle = ctx.getApplication().getResourceBundle(ctx, bundleVar);
            return bundle;
        }
    }

    public static ResourceBundle getResourceBundle(FacesContext ctx, Locale locale) {
        Map<Object, Object> attrs = ctx.getAttributes();
        ResourceBundle bundle = (ResourceBundle) attrs.get(KEY_RESOURCE_BUNDLE);
        if (bundle == null) {
            bundle = getResourceBundleImpl(ctx, locale);
            attrs.put(KEY_RESOURCE_BUNDLE, bundle);
        }
        return bundle;
    }

    public static ResourceBundle getResourceBundle(FacesContext ctx) {
        return getResourceBundle(ctx, FacesLocale.getLocale(ctx));
    }

    public static ResourceBundle getResourceBundle() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        return getResourceBundle(ctx, FacesLocale.getLocale(ctx));
    }
}
