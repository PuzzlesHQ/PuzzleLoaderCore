package com.github.puzzle.loader.provider.mixin;

import com.github.puzzle.loader.launch.PuzzleClassLoader;
import org.spongepowered.asm.service.IClassTracker;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Extracted and Modified from Mixins **/
final class PuzzleClassLoaderUtil implements IClassTracker {
    public static final String CACHED_CLASSES_FIELD = "cachedClasses";
    public static final String INVALID_CLASSES_FIELD = "invalidClasses";
    public static final String CLASS_LOADER_EXCEPTIONS_FIELD = "classLoaderExceptions";
    public static final String TRANSFORMER_EXCEPTIONS_FIELD = "transformerExceptions";
    
    /**
     * ClassLoader for this util
     */
    private final PuzzleClassLoader classLoader;
    
    // Reflected fields
    private final Map<String, Class<?>> cachedClasses;
    private final Set<String> invalidClasses;
    private final Set<String> classLoaderExceptions;
    private final Set<String> transformerExceptions;

    /**
     * Singleton, use factory to get an instance
     * 
     * @param classLoader class loader
     */
    PuzzleClassLoaderUtil(PuzzleClassLoader classLoader) {
        this.classLoader = classLoader;
        this.cachedClasses = PuzzleClassLoaderUtil.getField(classLoader, PuzzleClassLoaderUtil.CACHED_CLASSES_FIELD);
        this.invalidClasses = PuzzleClassLoaderUtil.getField(classLoader, PuzzleClassLoaderUtil.INVALID_CLASSES_FIELD);
        this.classLoaderExceptions = PuzzleClassLoaderUtil.getField(classLoader, PuzzleClassLoaderUtil.CLASS_LOADER_EXCEPTIONS_FIELD);
        this.transformerExceptions = PuzzleClassLoaderUtil.getField(classLoader, PuzzleClassLoaderUtil.TRANSFORMER_EXCEPTIONS_FIELD);
    }

    /**
     * Get the classloader
     */
    PuzzleClassLoader getClassLoader() {
        return this.classLoader;
    }
    
    /**
     * Get whether a class name exists in the cache (indicating it was loaded
     * via the inner loader
     * 
     * @param name class name
     * @return true if the class name exists in the cache
     */
    @Override
    public boolean isClassLoaded(String name) {
        return this.cachedClasses.containsKey(name);
    }

    /* (non-Javadoc)
     * @see org.spongepowered.asm.service.IMixinService#getClassRestrictions(
     *      java.lang.String)
     */
    @Override
    public String getClassRestrictions(String className) {
        String restrictions = "";
        if (this.isClassClassLoaderExcluded(className, null)) {
            restrictions = "PACKAGE_CLASSLOADER_EXCLUSION";
        }
        if (this.isClassTransformerExcluded(className, null)) {
            restrictions = (!restrictions.isEmpty() ? restrictions + "," : "") + "PACKAGE_TRANSFORMER_EXCLUSION";
        }
        return restrictions;
    }

    /**
     * Get whether the specified name or transformedName exist in either of the
     * exclusion lists
     * 
     * @param name class name
     * @param transformedName transformed class name
     * @return true if either exclusion list contains either of the names
     */
    boolean isClassExcluded(String name, String transformedName) {
        return this.isClassClassLoaderExcluded(name, transformedName) || this.isClassTransformerExcluded(name, transformedName);
    }

    /**
     * Get whether the specified name or transformedName exist in the
     * classloader exclusion list
     * 
     * @param name class name
     * @param transformedName transformed class name
     * @return true if the classloader exclusion list contains either of the
     *      names
     */
    boolean isClassClassLoaderExcluded(String name, String transformedName) {
        for (final String exception : this.getClassLoaderExceptions()) {
            if ((transformedName != null && transformedName.startsWith(exception)) || name.startsWith(exception)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * Get whether the specified name or transformedName exist in the
     * transformer exclusion list
     * 
     * @param name class name
     * @param transformedName transformed class name
     * @return true if the transformer exclusion list contains either of the
     *      names
     */
    boolean isClassTransformerExcluded(String name, String transformedName) {
        for (final String exception : this.getTransformerExceptions()) {
            if ((transformedName != null && transformedName.startsWith(exception)) || name.startsWith(exception)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Stuff a class name directly into the invalidClasses set, this prevents
     * the loader from classloading the named class. This is used by the mixin
     * processor to prevent classloading of mixin classes
     * 
     * @param name class name
     */
    @Override
    public void registerInvalidClass(String name) {
        if (this.invalidClasses != null) {
            this.invalidClasses.add(name);
        }
    }
    
    /**
     * Get the classloader exclusions from the target classloader
     */
    Set<String> getClassLoaderExceptions() {
        return this.classLoaderExceptions == null ? Collections.emptySet() : this.classLoaderExceptions;
    }
    
    /**
     * Get the transformer exclusions from the target classloader
     */
    Set<String> getTransformerExceptions() {
        return this.transformerExceptions == null ? Collections.emptySet() : this.transformerExceptions;
    }

    @SuppressWarnings("unchecked")
    private static <T> @Nullable T getField(PuzzleClassLoader classLoader, String fieldName) {
        try {
            Field field = PuzzleClassLoader.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T)field.get(classLoader);
        } catch (Exception ignored) {}
        return null;
    }
}
