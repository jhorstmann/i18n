package net.jhorstmann.i18n;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ServiceLoaderHelper {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoaderHelper.class);

    static <T> List<T> load(ClassLoader cl, Class<T> factoryClass) {
        if (log.isDebugEnabled()) {
            String clname = cl.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(cl));
            log.debug("Loading {} implementations for ClassLoader {}", factoryClass.getName(), clname);
        }
        ServiceLoader<T> loader = ServiceLoader.load(factoryClass);
        List<T> result = new ArrayList<T>();
        Iterator<T> iterator = loader.iterator();
        while (iterator.hasNext()) {
            T factory = iterator.next();
            result.add(factory);
        }
        return result;
    }
}
