package net.jhorstmann.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {

    private I18N() {
    }
    
    public static Locale getLocale() {
        LocaleProvider localeProvider = LocaleProviderFactory.newInstance().newLocaleProvider();
        return localeProvider.getLocale();
    }
    
    public static ResourceBundle getBundle(Locale locale) {
        ResourceBundleProvider bundleProvider = ResourceBundleProviderFactory.newInstance().newResourceBundleProvider();
        return bundleProvider.getResourceBundle(locale);
    }

    public static ResourceBundle getBundle() {
        Locale locale = getLocale();
        return getBundle(locale);
    }
    
    private static String format(ResourceBundle bundle, String pattern, Object... params) {
        if (params.length == 0) {
            // FIXME: This should be faster but does give different results
            // when there are placeholders without corresponding parameters.
            return pattern;
        } else {
            MessageFormat fmt = new MessageFormat(pattern, bundle.getLocale());
            return fmt.format(params);
        }
    }

    public static String translate(ResourceBundle bundle, String context, String message, String plural, long n, Object... params) {
        if (context != null) {
            if (plural != null) {
                return trnc(bundle, context, message, plural, n, params);
            } else {
                return trc(bundle, context, message, params);
            }
        } else {
            if (plural != null) {
                return trn(bundle, message, plural, n, params);
            } else {
                return tr(bundle, message, params);
            }
        }
    }
    
    public static String mark(String message) {
        return message;
    }

    public static String tr(String message, Object... params) {
        return tr(getBundle(), message, params);
    }

    public static String tr(Locale locale, String message, Object... params) {
        return tr(getBundle(locale), message, params);
    }

    public static String tr(ResourceBundle bundle, String message, Object... params) {
        return format(bundle, GettextResourceBundle.gettext(bundle, message), params);
    }

    public static String trc(String context, String message, Object... params) {
        return trc(getBundle(), context, message, params);
    }
    
    public static String trc(Locale locale, String context, String message, Object... params) {
        return trc(getBundle(locale), context, message, params);
    }
    
    public static String trc(ResourceBundle bundle, String context, String message, Object... params) {
        return format(bundle, GettextResourceBundle.pgettext(bundle, context, message), params);
    }
    
    public static String trn(String message, String plural, long n, Object... params) {
        return trn(getBundle(), message, plural, n, params);
    }

    public static String trn(Locale locale, String message, String plural, long n, Object... params) {
        return trn(getBundle(locale), message, plural, n, params);
    }

    public static String trn(ResourceBundle bundle, String message, String plural, long n, Object... params) {
        String text = GettextResourceBundle.ngettext(bundle, message, plural, n);
        return format(bundle, text, params);
    }
    
    public static String trnc(String context, String message, String plural, long n, Object... params) {
        return trnc(getBundle(), context, message, plural, n, params);
    }

    public static String trnc(Locale locale, String context, String message, String plural, long n, Object... params) {
        return trnc(getBundle(locale), context, message, plural, n, params);
    }

    public static String trnc(ResourceBundle bundle, String context, String message, String plural, long n, Object... params) {
        String text = GettextResourceBundle.npgettext(bundle, context, message, plural, n);
        return format(bundle, text, params);
    }
    
}
