package net.jhorstmann.i18n.jsf;

import java.util.Locale;
import javax.faces.context.FacesContext;

public class FacesLocale {

    private FacesLocale() {

    }

    public static Locale getLocale(FacesContext ctx) {
        if (ctx != null) {
            return ctx.getViewRoot().getLocale();
        } else {
            return Locale.getDefault();
        }
    }

    public static Locale getLocale() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        return getLocale(ctx);
    }
}
