package net.jhorstmann.i18n;

import net.jhorstmann.i18n.impl.DefaultResourceBundleProviderFactory;
import java.util.Iterator;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ResourceBundleProviderFactory {

    private static final Logger log = LoggerFactory.getLogger(ResourceBundleProviderFactory.class);
    private static final ResourceBundleProviderFactory DEFAULT_INSTANCE = new DefaultResourceBundleProviderFactory();
    
    private static ResourceBundleProviderFactory initialValue(ClassLoader cl) {
        if (log.isDebugEnabled()) {
            String clname = cl.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(cl));
            log.debug("Loading {} for ClassLoader {}", ResourceBundleProviderFactory.class.getName(), clname);
        }
        ServiceLoader<ResourceBundleProviderFactory> loader = ServiceLoader.load(ResourceBundleProviderFactory.class, cl);
        Iterator<ResourceBundleProviderFactory> iterator = loader.iterator();
        while (iterator.hasNext()) {
            ResourceBundleProviderFactory factory = iterator.next();
            if (factory.isEnvironmentSupported()) {
                log.debug("Using {}", factory.getClass().getName());
                return factory;
            } else {
                log.info("{} is not supported in this environment", factory.getClass().getName());
            }
        }
        return DEFAULT_INSTANCE;
    }
    
    private static ResourceBundleProviderFactory instance;

    /**
     * Loads a {@link ResourceBundleProviderFactory} using Java's {@link java.util.ServiceLoader} infrastructure.
     * @return 
     */
    public static ResourceBundleProviderFactory newInstance() {
        if (instance == null) {
            instance = initialValue(ResourceBundleProviderFactory.class.getClassLoader());
        }
        return instance;
    }

    /**
     * Return whether this {@link ResourceBundleProviderFactory} is usable in this environment.
     * This makes it possible to configure multiple implementations in a project where
     * one is used when run as a webapp and another one for scripts and tests.
     * @return 
     */
    public abstract ResourceBundleProvider newResourceBundleProvider();

    public abstract boolean isEnvironmentSupported();
}
