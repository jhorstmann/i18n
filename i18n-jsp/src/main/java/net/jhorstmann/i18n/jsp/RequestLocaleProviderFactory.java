package net.jhorstmann.i18n.jsp;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import net.jhorstmann.i18n.LocaleProvider;
import net.jhorstmann.i18n.LocaleProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLocaleProviderFactory extends LocaleProviderFactory {

    @Override
    public boolean isEnvironmentSupported() {
        return I18nFilter.getCurrentRequest() != null;
    }

    @Override
    public LocaleProvider newLocaleProvider() {
        return RequestLocaleProvider.INSTANCE;
    }
}
