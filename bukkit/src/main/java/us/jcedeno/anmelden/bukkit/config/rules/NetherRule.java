package us.jcedeno.anmelden.bukkit.config.rules;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;

import lombok.extern.log4j.Log4j2;
import us.jcedeno.anmelden.bukkit.config.ConfigManager;
import us.jcedeno.anmelden.bukkit.config.annotations.Setting;
import us.jcedeno.anmelden.bukkit.config.impl.GSetting;

/**
 * A rule that controls the nether.
 * 
 * @author thejcedeno
 */
@Setting(name = "nether", description = "Controls the nether.")
@Log4j2
public class NetherRule extends GSetting implements Listener {

    /**
     * Constructor required by the Setting annotation.
     * 
     * @param name        the name of the rule.
     * @param description the description of the rule.
     */
    public NetherRule(String name, String description) {
        super(name, description);
        // hello world
        log.info("Nether rule initialized.");
    }

    @EventHandler
    public void disablePortalCreation(final PortalCreateEvent event) {
        // If this listener is registered, it means by the global contex that the
        // current state is on, so everything written here assumes SETTING = ON.
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPortalEvent(final PlayerPortalEvent e) {
        // Allow OP override for debug reasons and what not.
        if (e.getPlayer().hasPermission("monad.uhc.nether.override")) {
            log.info("Player " + e.getPlayer().getName() + " overrode the nether rule.");
            return;
        }
        // Otherwise cancel it.
        e.setCancelled(true);
    }

    @Override
    public boolean toggle(ConfigManager ruleManager) {
        System.out.println("Hi 🤓");
        return new Random().nextBoolean();
    }

}
