package us.jcedeno.anmelden.bukkit;

import org.bukkit.plugin.java.JavaPlugin;

public interface Game<T extends JavaPlugin> {

    public T instance();

    public void instance(T instance);

    /**
     * TODO: Figure out how to properly abstract context. It has to be a class that
     * implements a "serializable" interface and that's about it (I think).
     */

     
}
