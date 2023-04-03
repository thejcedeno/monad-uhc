package us.jcedeno.anmelden.bukkit.scenarios.models;

import org.bukkit.Material;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class InstantiatedScenario extends BaseScenario {

    public InstantiatedScenario(String name, String description, Material material) {
        super(name, description, material);
    }

     /**
     * Required for auto registration
     */
    @Override
    public void enable() {
        log.info("[✅] " + this.name() + " scenario enabled");
    }

    /**
     * Required for auto registration.
     */
    @Override
    public void disable() {
        log.info("[❌] " + this.name() + " scenario disabled");
    }
    
}
