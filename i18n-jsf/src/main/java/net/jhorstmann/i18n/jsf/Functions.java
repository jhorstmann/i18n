package net.jhorstmann.i18n.jsf;

import net.jhorstmann.i18n.I18N;

// import static net.jhorstmann.i18n.jsf.FacesResourceBundle.getResourceBundle;
// patched: at first try to resolve a provider with jdk-serviceloader over I18N-class...
import static net.jhorstmann.i18n.I18N.getBundle;

public class Functions {
    private Functions() {
    }

    public static String mark(String message) {
        return message;
    }

    public static String tr(String message) {
        return I18N.tr(getBundle(), message);
    }

    public static String tr(String message, Object param) {
        return I18N.tr(getBundle(), message, param);
    }

    public static String tr(String message, Object param1, Object param2) {
        return I18N.tr(getBundle(), message, param1, param2);
    }

    public static String tr(String message, Object param1, Object param2, Object param3) {
        return I18N.tr(getBundle(), message, param1, param2, param3);
    }

    public static String tr(String message, Object param1, Object param2, Object param3, Object param4) {
        return I18N.tr(getBundle(), message, param1, param2, param3, param4);
    }

    public static String trc(String context, String message) {
        return I18N.trc(getBundle(), context, message);
    }

    public static String trc(String context, String message, Object param) {
        return I18N.trc(getBundle(), context, message, param);
    }

    public static String trc(String context, String message, Object param1, Object param2) {
        return I18N.trc(getBundle(), context, message, param1, param2);
    }

    public static String trc(String context, String message, Object param1, Object param2, Object param3) {
        return I18N.trc(getBundle(), context, message, param1, param2, param3);
    }

    public static String trc(String context, String message, Object param1, Object param2, Object param3, Object param4) {
        return I18N.trc(getBundle(), context, message, param1, param2, param3, param4);
    }

    public static String trn(String message, String plural, long n) {
        return I18N.trn(getBundle(), message, plural, n);
    }

    public static String trn(String message, String plural, long n, Object param) {
        return I18N.trn(getBundle(), message, plural, n, param);
    }

    public static String trn(String message, String plural, long n, Object param1, Object param2) {
        return I18N.trn(getBundle(), message, plural, n, param1, param2);
    }

    public static String trn(String message, String plural, long n, Object param1, Object param2, Object param3) {
        return I18N.trn(getBundle(), message, plural, n, param1, param2, param3);
    }

    public static String trn(String message, String plural, long n, Object param1, Object param2, Object param3, Object param4) {
        return I18N.trn(getBundle(), message, plural, n, param1, param2, param3, param4);
    }

    public static String trnc(String context, String message, String plural, long n) {
        return I18N.trnc(getBundle(), context, message, plural, n);
    }

    public static String trnc(String context, String message, String plural, long n, Object param) {
        return I18N.trnc(getBundle(), context, message, plural, n, param);
    }

    public static String trnc(String context, String message, String plural, long n, Object param1, Object param2) {
        return I18N.trnc(getBundle(), context, message, plural, n, param1, param2);
    }

    public static String trnc(String context, String message, String plural, long n, Object param1, Object param2, Object param3) {
        return I18N.trnc(getBundle(), context, message, plural, n, param1, param2, param3);
    }

    public static String trnc(String context, String message, String plural, long n, Object param1, Object param2, Object param3, Object param4) {
        return I18N.trnc(getBundle(), context, message, plural, n, param1, param2, param3, param4);
    }
}
