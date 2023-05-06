package us.jcedeno.anmelden.bukkit.whitelist.listener;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import us.jcedeno.anmelden.bukkit.MonadUHC;

public class WhitelistListeners implements Listener{

    @EventHandler
    public void onLogin(final AsyncPlayerPreLoginEvent e){

        final var wm = MonadUHC.instance().getWhitelistManager();
        // return early if wl off.
        if(!wm.whitelistEnabled()){
            return;
        }

        final var uid = e.getUniqueId();
        Bukkit.getPlayer(uid);
        
        if(!wm.getWhitelist().contains(uid)){
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "You are not whitelisted");
            
        }
    }

    
}
