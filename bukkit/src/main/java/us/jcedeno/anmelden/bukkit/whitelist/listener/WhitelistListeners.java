package us.jcedeno.anmelden.bukkit.whitelist.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.anmelden.bukkit.MonadUHC;

/**
 * A listener class that holds whitelist related logic.
 * 
 * @author jcedeno
 */
public class WhitelistListeners implements Listener {

    @EventHandler
    public void onLoginHandleWL(final PlayerLoginEvent e) {
        final var wm = MonadUHC.instance().getWhitelistManager();

        if (!wm.isEnabled())
            return;

        final var p = e.getPlayer();

        if (p.hasPermission("uhc.whitelist.bypass") || wm.isWhitelisted(p.getUniqueId()))
            return;

        e.disallow(Result.KICK_WHITELIST,
                MiniMessage.miniMessage().deserialize("<red>You are not in the whitelist! \nHoe."));
    }

}
