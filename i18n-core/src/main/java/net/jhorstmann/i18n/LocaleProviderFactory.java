package net.jhorstmann.i18n;

import java.util.Iterator;
import java.util.ServiceLoader;
import net.jhorstmann.i18n.impl.DefaultLocaleProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LocaleProviderFactory {

    private static final Logger log = LoggerFactory.getLogger(LocaleProviderFactory.class);
    private static final LocaleProviderFactory DEFAULT_INSTANCE = new DefaultLocaleProviderFactory();

    private static LocaleProviderFactory initialValue(ClassLoader cl) {
        if (log.isDebugEnabled()) {
            String clname = cl.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(cl));
            log.debug("Loading {} for ClassLoader {}", LocaleProviderFactory.class.getName(), clname);
        }
        ServiceLoader<LocaleProviderFactory> loader = ServiceLoader.load(LocaleProviderFactory.class);
        Iterator<LocaleProviderFactory> iterator = loader.iterator();
        while (iterator.hasNext()) {
            LocaleProviderFactory factory = iterator.next();
            if (factory.isEnvironmentSupported()) {
                log.debug("Using {}", factory.getClass().getName());
                return factory;
            } else {
                log.info("{} is not supported in this environment", factory.getClass().getName());
            }
        }
        return DEFAULT_INSTANCE;
    }
    
    private static LocaleProviderFactory instance;

    /**
     * Loads a {@link LocaleProviderFactory} using Java's {@link java.util.ServiceLoader} infrastructure.
     * @return 
     */
    public static LocaleProviderFactory newInstance() {
        if (instance == null) {
            instance = initialValue(LocaleProviderFactory.class.getClassLoader());
        }
        return instance;
    }

    /**
     * Return whether this {@link LocaleProviderFactory} is usable in this environment.
     * This makes it possible to configure multiple implementations in a project where
     * one is used when run as a webapp and another one for scripts and tests.
     * @return 
     */
    public abstract boolean isEnvironmentSupported();

    public abstract LocaleProvider newLocaleProvider();
}
