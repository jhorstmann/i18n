package net.jhorstmann.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ResourceBundleProvider {
    public ResourceBundle getResourceBundle(Locale locale);
}
