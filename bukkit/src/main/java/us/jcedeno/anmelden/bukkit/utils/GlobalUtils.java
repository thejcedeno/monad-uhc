package us.jcedeno.anmelden.bukkit.utils;

public class GlobalUtils {

    public static class SerializedObject<T> {
        private final T object;

        public SerializedObject(T object) {
            this.object = object;
        }

        public T getObject() {
            return object;
        }

        public String getSerialized(){
            return object.toString();
        }
    }

}
