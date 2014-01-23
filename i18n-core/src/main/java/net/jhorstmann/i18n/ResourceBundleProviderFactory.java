package net.jhorstmann.i18n;

import net.jhorstmann.i18n.impl.DefaultResourceBundleProviderFactory;

import java.util.List;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ResourceBundleProviderFactory {

	private static final Logger log = LoggerFactory.getLogger(ResourceBundleProviderFactory.class);
	
    private static final ResourceBundleProviderFactory DEFAULT_INSTANCE = new DefaultResourceBundleProviderFactory();
    private static List<ResourceBundleProviderFactory> instances;

    /**
     * Loads a {@link ResourceBundleProviderFactory} using Java's {@link java.util.ServiceLoader} infrastructure.
     * @return 
     */
    public static ResourceBundleProviderFactory newInstance() {
        if (instances == null) {
            instances = ServiceLoaderHelper.load(ResourceBundleProviderFactory.class.getClassLoader(), ResourceBundleProviderFactory.class);
            log.debug("Found {} ResourceBundleProviderFactories",instances.size());
        }
        for (ResourceBundleProviderFactory factory : instances) {
            if (factory.isEnvironmentSupported()) {
                return factory;
            }
        }
        log.debug("no registered ResourceBundleProviderFactory found, returning default provider-factory");
        return DEFAULT_INSTANCE;
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
