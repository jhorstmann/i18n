package net.jhorstmann.i18n.jsp;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class I18nFilter implements Filter {
    static final String KEY_RESOURCE_BUNDLE_NAME = "net.jhorstmann.i18n.ResourceBundleName";
    static final String KEY_RESOURCE_BUNDLE = "net.jhorstmann.i18n.ResourceBundle";
    static final String PARAM_RESOURCE_BUNDLE = "bundle";

    private static final ThreadLocal<HttpServletRequest> currentRequest = new ThreadLocal<HttpServletRequest>();

    private String resourceBundleName;
    
    static HttpServletRequest getCurrentRequest() {
        return currentRequest.get();
    }

    @Override
    public void init(FilterConfig fc) throws ServletException {
        String name = fc.getInitParameter("bundle");
        if (name == null || name.length() == 0) {
            throw new ServletException("Not ResourceBundle specified as initialization parameter");
        }
        resourceBundleName = name;
    }

    @Override
    public void doFilter(ServletRequest sr, ServletResponse sr1, FilterChain fc) throws IOException, ServletException {
        if (sr instanceof HttpServletRequest) {
            sr.setAttribute(KEY_RESOURCE_BUNDLE_NAME, resourceBundleName);
            currentRequest.set((HttpServletRequest)sr);
            try {
                fc.doFilter(sr, sr1);
            } finally {
                currentRequest.remove();
            }
        } else {
            fc.doFilter(sr, sr1);
        }
    }

    @Override
    public void destroy() {
    }
}
