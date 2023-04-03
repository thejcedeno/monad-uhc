package us.jcedeno.anmelden.bukkit.scenarios.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import us.jcedeno.anmelden.bukkit.scenarios.annotations.Scenario;
import us.jcedeno.anmelden.bukkit.scenarios.models.ListenerScenario;

@Scenario(name = "NoFall", description = "No fall damage", ui = Material.FEATHER)
public class NoFall extends ListenerScenario {

    public NoFall(String name, String description, Material material) {
        super(name, description, material);
    }

    @EventHandler
    public void onDamge(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                e.setCancelled(true);
            }
        }
    }

}
