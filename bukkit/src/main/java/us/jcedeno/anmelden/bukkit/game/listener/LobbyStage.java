package us.jcedeno.anmelden.bukkit.game.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.anmelden.bukkit.MonadUHC;

public class LobbyStage implements Listener {

    @EventHandler
    public void onJoinLobbyInfo(final PlayerJoinEvent e) {
        final var loc = MonadUHC.instance().getLocationManager().getLobbySpawnPoint();
        final var p = e.getPlayer();

        p.teleport(loc);
        p.sendMessage(MiniMessage.miniMessage().deserialize("<green>Welcome to UHC!"));

    }

    @EventHandler
    public void onBlockBreakLobby(final BlockBreakEvent e) {
        if (!e.getBlock().getWorld().getName().equalsIgnoreCase("lobby"))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlaceLobby(final BlockPlaceEvent e) {
        if (!e.getBlock().getWorld().getName().equalsIgnoreCase("lobby"))
            return;

        e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTakesDamage(final EntityDamageEvent e) {
        if (e.getEntityType() != EntityType.PLAYER)
            return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onInteraction(final PlayerInteractEvent e) {
        if (!e.getPlayer().getWorld().getName().equalsIgnoreCase("lobby") && e.getAction() != Action.PHYSICAL)
            return;

        e.setCancelled(true);

    }

    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent e) {
        e.setCancelled(true);
        e.getEntity().setFoodLevel(20);
        e.getEntity().setSaturation(20);
    }
}
