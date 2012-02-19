package net.jhorstmann.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class GettextResourceBundle extends ResourceBundle {

    private static final String CONTEXT_GLUE = "\u0004";

    protected abstract int pluralIndex(long n);

    private static String gettextnull(ResourceBundle bundle, String msgid) {
        Object val;
        try {
            val = bundle.getObject(msgid);
            if (val instanceof String[]) {
                String[] strings = (String[]) val;
                return strings[0];
            } else {
                return (String)val;
            }
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    public static String gettext(ResourceBundle bundle, String msgid) {
        String msg = gettextnull(bundle, msgid);
        return msg != null ? msg : msgid;
    }

    public static String pgettext(ResourceBundle bundle, String msgctx, String msgid) {
        String msg = gettextnull(bundle, msgctx+CONTEXT_GLUE+msgid);
        return msg != null ? msg : msgid;
    }

    private static String ngettextnull(ResourceBundle bundle, String msgid, String msgidPlural, long n) {
        Object val;
        try {
            val = bundle.getObject(msgid);
        } catch (MissingResourceException ex) {
            return null;
        }
        if (val instanceof String[]) {
            String[] plurals = (String[]) val;
            if (bundle instanceof GettextResourceBundle) {
                int idx = ((GettextResourceBundle)bundle).pluralIndex(n);
                if (idx < 0 || idx >= plurals.length) {
                    return plurals[0];
                } else {
                    return plurals[idx];
                }
            } else {
                return plurals[0];
            }
        } else {
            return (String) val;
        }
    }

    public static String ngettext(ResourceBundle bundle, String msgid, String msgidPlural, long n) {
        String msg = ngettextnull(bundle, msgid, msgidPlural, n);
        if (msg != null) {
            return msg;
        } else {
            return n != 1 ? msgidPlural : msgid;
        }
    }

    public static String npgettext(ResourceBundle bundle, String msgctx, String msgid, String msgidPlural, long n) {
        String msg = ngettextnull(bundle, msgctx + CONTEXT_GLUE + msgid, msgidPlural, n);
        if (msg != null) {
            return msg;
        } else {
            return n != 1 ? msgidPlural : msgid;
        }
    }
}
