package us.jcedeno.anmelden.bukkit._utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
}
