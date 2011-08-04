package net.jhorstmann.i18n.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.WeakHashMap;
import net.jhorstmann.i18n.I18N;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultResourceBundle {
    private static final Logger log = LoggerFactory.getLogger(DefaultResourceBundle.class);
    private static final String PATH = "META-INF/services/" + I18N.class.getPackage().getName() + ".ResourceBundle";
    private static final WeakHashMap<ClassLoader, StringRef> bundleName = new WeakHashMap<ClassLoader, StringRef>();

    /**
     * This allows us to cache nonexisting bundle names.
     */
    static final class StringRef {
        private static final StringRef NULL = new StringRef(null);
        private final String value;
        
        static StringRef valueOf(String value) {
            return value == null ? NULL : new StringRef(value);
        }

        private StringRef(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    static String loadBundleName(ClassLoader cl) {
        log.debug("Loading resource bundle name from {}", PATH);
        try {
            Enumeration<URL> resources = cl.getResources(PATH);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                InputStream in = null;
                try {
                    in = url.openStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(in));
                    String line = br.readLine();
                    if (line != null) {
                        return line;
                    }
                } catch (IOException ex) {
                    log.info("Could not read bundle name from " + url.toString(), ex);
                } finally {
                    if (in != null) {
                        in.close();
                    }
                }
            }
            return null;
        } catch (IOException ex) {
            log.info("IOException while enumerating bundle names", ex);
            return null;
        }
    }

    public static String getBundleName(ClassLoader cl) {
        StringRef res = bundleName.get(cl);
        if (res == null) {
            res = StringRef.valueOf(loadBundleName(cl));
            bundleName.put(cl, res);
        }
        return res.getValue();
    }
    
    public static String getBundleName() {
        return getBundleName(Thread.currentThread().getContextClassLoader());
    }

    public static void setBundleName(ClassLoader cl, String name) {
        bundleName.put(cl, new StringRef(name));
    }
    
    public static void setBundleName(String name) {
        setBundleName(Thread.currentThread().getContextClassLoader(), name);
    }
}
