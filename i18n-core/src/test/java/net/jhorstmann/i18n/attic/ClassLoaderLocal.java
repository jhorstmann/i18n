package net.jhorstmann.i18n.attic;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public abstract class ClassLoaderLocal<T> {

    private final Map<ClassLoader, WeakReference<T>> map = new WeakHashMap<ClassLoader, WeakReference<T>>();

    static class EmptyClassLoader extends ClassLoader {

        EmptyClassLoader() {
            super(null);
        }
        
    }
    private static final ClassLoader NULL_KEY = new EmptyClassLoader();
    
    protected abstract T initialValue(ClassLoader cl);

    private synchronized T getInitial(ClassLoader cl) {
        T value = initialValue(cl);
        if (value == null) {
            throw new IllegalStateException("Initial value must not be null");
        }
        map.put(cl, new WeakReference<T>(value));
        return value;
    }

    public final synchronized T get(ClassLoader cl) {
        if (cl == null) {
            cl = NULL_KEY;
        }
        WeakReference<T> ref = map.get(cl);
        T value = null;
        if (ref == null) {
            return getInitial(cl);
        } else {
            value = ref.get();
            if (value != null) {
                return value;
            } else {
                return getInitial(cl);
            }
        }
    }

    public final synchronized T get() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return get(cl);
    }
}
