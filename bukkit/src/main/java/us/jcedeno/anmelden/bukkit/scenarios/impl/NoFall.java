package us.jcedeno.anmelden.bukkit.scenarios.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import us.jcedeno.anmelden.bukkit.scenarios.annotations.Scenario;
import us.jcedeno.anmelden.bukkit.scenarios.models.BaseScenario;

@Scenario(name = "NoFall", description = "No fall damage", ui = Material.FEATHER)
public class NoFall extends BaseScenario implements Listener {

    public NoFall(String name, String description) {
        super(name, description);
    }

    @EventHandler
    public void onDamge(EntityDamageEvent e){
        if(e.getEntity() instanceof Player p ){ 
            if(e.getCause() == EntityDamageEvent.DamageCause.FALL){
                e.setCancelled(true);
            }
        }
    }

    @Override
    public void enable() {
    }
    

    @Override
    public void disable() {
    }
    
}
