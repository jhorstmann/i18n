package net.jhorstmann.i18n.jsp;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import net.jhorstmann.i18n.LocaleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class RequestLocaleProvider implements LocaleProvider {
    private static final Logger log = LoggerFactory.getLogger(RequestLocaleProvider.class);
    static RequestLocaleProvider INSTANCE = new RequestLocaleProvider();

    public Locale extractLocale(HttpServletRequest request) {
        return request.getLocale();
    }

    @Override
    public Locale getLocale() {
        HttpServletRequest currentRequest = I18nFilter.getCurrentRequest();
        Locale locale = extractLocale(currentRequest);
        log.debug("Extracted locale {}", locale);
        return locale;
    }

}
