package us.jcedeno.anmelden.bukkit._utils;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import java.util.Set;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

import lombok.SneakyThrows;

public class GlobalUtils {

    public static class SerializedObject<T> {
        private final T object;

        public SerializedObject(T object) {
            this.object = object;
        }

        public T getObject() {
            return object;
        }

        public String getSerialized() {
            return object.toString();
        }
    }

    /**
     * A utility function to get all the classes from a package.
     * 
     * @param packageName The package name to get the classes from.
     * @return A list of all the classes in the package.
     */
    @SneakyThrows
    public static List<Class<?>> getClassesFromPackage(String packageName) {
        var classLoader = Thread.currentThread().getContextClassLoader();
        var path = packageName.replace('.', '/');

        var list = new ArrayList<Class<?>>();
        var resources = classLoader.getResources(path);

        while (resources.hasMoreElements()) {
            var resource = resources.nextElement();
            if (resource.getProtocol().equals("file")) {
                var directory = new File(resource.toURI());
                if (directory.exists()) {
                    var files = directory.listFiles();
                    for (var file : files) {
                        if (file.isFile() && file.getName().endsWith(".class")) {
                            String className = packageName + '.'
                                    + file.getName().substring(0, file.getName().length() - 6);
                            Class<?> clazz = Class.forName(className);

                            list.add(clazz);
                        }
                    }
                }
            }
        }
        return list;
    }

    public static Set<Class<?>> findAllClassesUsingGoogleGuice(String packageName) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        ClassPath classPath = ClassPath.from(classLoader);
        Set<Class<?>> classes = classPath.getTopLevelClassesRecursive(packageName)
                .stream()
                .map(ClassInfo::load)
                .collect(Collectors.toSet());
        return classes;

    }

    public static Set<Class<?>> findAnnotatedClasses(String packageName, Class<? extends Annotation> annotationClass) {
    Reflections reflections = new Reflections(packageName);
    Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(annotationClass);
    return annotatedClasses;
}


    
}
