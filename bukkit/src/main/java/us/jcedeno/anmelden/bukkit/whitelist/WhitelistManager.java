package us.jcedeno.anmelden.bukkit.whitelist;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class WhitelistManager {
    private List<UUID> whitelist = new ArrayList<>();
    private boolean enabled = false;

    public WhitelistManager(){
        log.info("whitelist manager initialized");
    }


    public void enableWhitelist() {
        log.info("whitelist enabled");
    }

    public void disableWhitelist() {
        log.info("whitelist disabled");
    }


    public boolean isWhitelisted(UUID uid) {
        return whitelist.contains(uid);
    }

    public boolean whitelistEnabled() {
        return this.enabled;
    }



    public List<UUID> getWhitelist() {
        return whitelist;
    }
}
