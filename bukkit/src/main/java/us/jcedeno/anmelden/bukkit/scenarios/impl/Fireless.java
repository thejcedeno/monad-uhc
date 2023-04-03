package us.jcedeno.anmelden.bukkit.scenarios.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import us.jcedeno.anmelden.bukkit.scenarios.annotations.Scenario;
import us.jcedeno.anmelden.bukkit.scenarios.models.ListenerScenario;

/**
 * A scenario that prevents fire damage on the overworld.
 * 
 * @author thejcedeno
 */
@Scenario(name = "Fireless", description = "No fire damage on the overworld!", ui = Material.FIRE_CHARGE)
public class Fireless extends ListenerScenario{
    
    public Fireless(String name, String description, Material material) {
        super(name, description, material);
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


}
