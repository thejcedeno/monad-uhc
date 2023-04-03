package us.jcedeno.anmelden.bukkit.scenarios.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import lombok.extern.log4j.Log4j2;
import us.jcedeno.anmelden.bukkit.scenarios.annotations.Scenario;
import us.jcedeno.anmelden.bukkit.scenarios.models.BaseScenario;

/**
 * A scenario that prevents fire damage on the overworld.
 * 
 * @author thejcedeno
 */
@Log4j2
@Scenario(name = "Fireless", description = "No fire damage on the overworld!", ui = Material.FIRE_CHARGE)
public class Fireless extends BaseScenario implements Listener {

    /**
     * Constructor required for auto registration.
     * 
     * @param name        Scenario name.
     * @param description Scenario description.
     */
    public Fireless(String name, String description) {
        super(name, description);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageEvent e) {
        // Cancell all player damage if the cause is fire, fire tick, or lava.
        if (e.getEntity() instanceof Player p)
            switch (e.getCause()) {
                case FIRE:
                case FIRE_TICK:
                case LAVA:
                    e.setCancelled(true);
                    break;
                default:
                    break;
            }
    }

    /**
     * Required for auto registration
     */
    @Override
    public void enable() {
        log.info("[✅] Fireless scenario enabled");
    }

    /**
     * Required for auto registration.
     */
    @Override
    public void disable() {
        log.info("[❌] Fireless scenario disabled");
    }

}
