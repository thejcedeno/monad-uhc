package us.jcedeno.anmelden.bukkit.whitelist;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import lombok.extern.log4j.Log4j2;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.whitelist.listener.WhitelistListeners;

/**
 * A manager for the whitelist module.
 * 
 * @author jcedeno
 */
@Log4j2
public class WhitelistManager {
    private final List<UUID> whitelist = new ArrayList<>();
    private final Listener wlListener;
    private boolean enabled = false;

    public WhitelistManager() {
        log.info("whitelist manager initialized");
        this.wlListener = new WhitelistListeners();
    }

    public void enableWhitelist() {
        log.info("whitelist enabled");
        Bukkit.getPluginManager().registerEvents(this.wlListener, MonadUHC.instance());
        enabled = true;
    }

    public void disableWhitelist() {
        log.info("whitelist disabled");
        HandlerList.unregisterAll(this.wlListener);
        enabled = false;
    }

    public boolean isWhitelisted(UUID uid) {
        return whitelist.contains(uid);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public List<UUID> getWhitelist() {
        return whitelist;
    }
}
