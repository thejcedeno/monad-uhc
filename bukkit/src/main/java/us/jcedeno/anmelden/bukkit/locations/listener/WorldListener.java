package us.jcedeno.anmelden.bukkit.locations.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import us.jcedeno.anmelden.bukkit.MonadUHC;

public class WorldListener implements Listener{

    private boolean initialLoadComplete = false;

    @EventHandler
    public void onWorldLoad(final WorldLoadEvent e){
        if(initialLoadComplete) return;

        initialLoadComplete = !initialLoadComplete;

        MonadUHC.instance().getLocationManager().getLobby();
    }
    
}
