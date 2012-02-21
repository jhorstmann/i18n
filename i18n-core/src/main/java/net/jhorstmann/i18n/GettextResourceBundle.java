package net.jhorstmann.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class GettextResourceBundle extends ResourceBundle {

    private static final String CONTEXT_GLUE = "\u0004";

    protected abstract int pluralIndex(long n);
    protected abstract Object lookup(String key);

    private static Object lookup(ResourceBundle bundle, String msgid) {
        try {
            // For a GettextResourceBundle we don't need the delegation to a
            // parent ResourceBundle done by getObject since we can just return the given msgid
            return bundle instanceof GettextResourceBundle
                ? ((GettextResourceBundle)bundle).lookup(msgid)
                : bundle.getObject(msgid);
        } catch (MissingResourceException ex) {
            return null;
        }
    }

    private static String lookupString(ResourceBundle bundle, String msgid) {
        Object val = lookup(bundle, msgid);
        if (val instanceof String[]) {
            String[] strings = (String[]) val;
            return strings[0];
        } else {
            return (String)val;
        }
    }

    public static String gettext(ResourceBundle bundle, String msgid) {
        String msg = lookupString(bundle, msgid);
        return msg != null ? msg : msgid;
    }

    public static String pgettext(ResourceBundle bundle, String msgctx, String msgid) {
        String msg = lookupString(bundle, msgctx+CONTEXT_GLUE+msgid);
        return msg != null ? msg : msgid;
    }

    private static String lookupPlural(ResourceBundle bundle, String msgid, String msgidPlural, long n) {
        Object val = lookup(bundle, msgid);
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
        String msg = lookupPlural(bundle, msgid, msgidPlural, n);
        if (msg != null) {
            return msg;
        } else {
            return n == 1 ? msgid : msgidPlural;
        }
    }

    public static String npgettext(ResourceBundle bundle, String msgctx, String msgid, String msgidPlural, long n) {
        String msg = lookupPlural(bundle, msgctx + CONTEXT_GLUE + msgid, msgidPlural, n);
        if (msg != null) {
            return msg;
        } else {
            return n == 1 ? msgid : msgidPlural;
        }
    }
}
