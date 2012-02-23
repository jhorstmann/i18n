package net.jhorstmann.i18n;

import java.util.List;
import java.util.ServiceLoader;
import net.jhorstmann.i18n.impl.DefaultLocaleProviderFactory;

public abstract class LocaleProviderFactory {

    private static final LocaleProviderFactory DEFAULT_INSTANCE = new DefaultLocaleProviderFactory();

    private static List<LocaleProviderFactory> instances;

    /**
     * Loads a {@link LocaleProviderFactory} using Java's {@link java.util.ServiceLoader} infrastructure.
     * @return 
     */
    public static LocaleProviderFactory newInstance() {
        if (instances == null) {
            instances = ServiceLoaderHelper.load(LocaleProviderFactory.class.getClassLoader(), LocaleProviderFactory.class);
        }
        for (LocaleProviderFactory factory : instances) {
            if (factory.isEnvironmentSupported()) {
                return factory;
            }
        }
        return DEFAULT_INSTANCE;
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
