package net.jhorstmann.i18n.jsp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import net.jhorstmann.i18n.I18N;
import net.jhorstmann.i18n.LocaleProvider;
import net.jhorstmann.i18n.ResourceBundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslationTag extends BodyTagSupport {

    private static final Logger log = LoggerFactory.getLogger(TranslationTag.class);
    private ResourceBundleProvider bundleProvider;
    private LocaleProvider localeProvider;
    private String context;
    private String message;
    private String plural;
    private Long num;
    private List params;

    @Override
    public void release() {
        super.release();
        bundleProvider = null;
        localeProvider = null;
    }

    @Override
    public int doStartTag() throws JspException {
        params = new ArrayList(2);
        if (bundleProvider == null) {
            bundleProvider = RequestResourceBundleProviderFactory.newInstance().newResourceBundleProvider();
        }
        if (localeProvider == null) {
            localeProvider = RequestLocaleProviderFactory.newInstance().newLocaleProvider();
        }
        return EVAL_BODY_BUFFERED;
    }

    @Override
    public int doEndTag() throws JspException {
        String msgid = getMessage();
        if (msgid == null) {
            BodyContent content = getBodyContent();
            if (content != null) {
                msgid = content.getString().trim();
                content.clearBody();
            }
        }
        if (msgid == null) {
            throw new JspTagException("No message given in TranslationTag");
        }
        Locale locale = localeProvider.getLocale();
        ResourceBundle bundle = bundleProvider.getResourceBundle(locale);

        if (log.isTraceEnabled()) {
            log.trace("localeProvider is {}", localeProvider);
            log.trace("locale is {}", locale);
            log.trace("bundleProvider is {}", bundleProvider);
            log.trace("bundle is {}", bundle);
            log.trace("context is '{}'", context);
            log.trace("message is '{}'" + message);
            log.trace("msgid is '{}'", msgid);
            log.trace("plural is '{}'", plural);
            log.trace("num is {}", num);
            log.trace("params is {}", params);
        }

        Object[] paramsArray = params == null ? new Object[0] : params.toArray();
        long n = num == null ? 0L : num.longValue();
        String msg = I18N.translate(bundle, context, msgid, plural, n, paramsArray);

        try {
            pageContext.getOut().print(msg);
        } catch (IOException ex) {
            throw new JspException(ex);
        }

        context = null;
        message = null;
        plural = null;
        num = null;

        return EVAL_PAGE;
    }

    public void addParam(Object param) {
        params.add(param);
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getNum() {
        return num;
    }

    public void setNum(Long num) {
        this.num = num;
    }

    public String getPlural() {
        return plural;
    }

    public void setPlural(String plural) {
        this.plural = plural;
    }
}
