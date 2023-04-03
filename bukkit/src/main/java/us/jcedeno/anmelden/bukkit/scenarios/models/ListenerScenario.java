package us.jcedeno.anmelden.bukkit.scenarios.models;

import org.bukkit.Material;
import org.bukkit.event.Listener;

/**
 * An abstraction layer of an instantiated scenario that also registersa a listener.
 * 
 * @author jcedeno
 */
public class ListenerScenario extends InstantiatedScenario implements Listener{

    public ListenerScenario(String name, String description, Material material) {
        super(name, description, material);
    }
    
}
