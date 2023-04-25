package us.jcedeno.anmelden.bukkit.config.rules;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.anmelden.bukkit.config.ConfigManager;
import us.jcedeno.anmelden.bukkit.config.annotations.Setting;
import us.jcedeno.anmelden.bukkit.config.impl.GSetting;

/**
 * A class that contains all the rules for the potions scenario.
 * 
 * @author thejcedeno
 */
@Setting(name = "potions", description = "Controls the use of potions. Weather All potions are allowed, or only tier 1 potions.")
public class PotionsRules extends GSetting implements Listener {

    /**
     * Creates a new PotionsRules instance.
     * 
     * @param name        the name of the rule.
     * @param description the description of the rule.
     */
    public PotionsRules(String name, String description) {
        super(name, description);
    }

    @EventHandler
    public void onClickBrewingStand(final PlayerInteractEvent e) {
        var b = e.getClickedBlock();

        if (b == null)
            return;

        if (b.getType() == Material.BREWING_STAND) {
            var p = e.getPlayer();

            p.sendMessage(MiniMessage.miniMessage().deserialize("<red>You can't use brewing stands!"));
            p.playSound(p.getLocation(), "minecraft:entity.villager.no", 1, 1);
            
            e.setCancelled(true);
        }
    }

    @Override
    public boolean toggle(ConfigManager ruleManager) {
        System.out.println("Hi 🤓");
        return new Random().nextBoolean();
    }
}
