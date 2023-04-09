package us.jcedeno.anmelden.bukkit.game;

import java.util.HashMap;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.game.models.Game;

/**
 * A class to manage all the interactions with the game object, the game loop
 * and the other systems.
 * 
 * @author jcedeno
 */
@Log4j2
public class GameManager implements Listener {

    protected Game game;

    @EventHandler
    public void onPlayerDmgCancel(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            e.setCancelled(true);
        }
    }

    public GameManager() {
        this.game = Game.of();
        game.setFinalHealTime(120);

        var actions = new HashMap<Integer, Consumer<Game>>();

        // Final heal
        actions.put(game.finalHealTime(), c -> {
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<gold>Final Heal!"));
            Bukkit.getOnlinePlayers().stream().forEach(p -> {
                p.setHealth(20);
                // send a message
                p.sendMessage(MiniMessage.miniMessage().deserialize("<green>You have been healed!"));
            });
        });
        // Pvp Time
        actions.put(game.pvpTime(), c -> {
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<gold>PvP has been enabled!"));
            // TODO: Implement PVP toggle event.
            Bukkit.getOnlinePlayers().stream().forEach(p -> {
                p.playSound(p.getLocation(), "minecraft:block.anvil.place", 1, 1);
            });
        });

        actions.put(15, c -> {
            HandlerList.unregisterAll(this);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<gold>Player Damage has been enabled!"));
        });
        // What happens at second 0
        actions.put(0, g -> {
            Bukkit.getPluginManager().registerEvents(this, MonadUHC.instance());
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<gold>Game has begun!"));
            Bukkit.getOnlinePlayers().stream().forEach(p -> {
                MonadUHC.instance().getPlayerManager().registerGamePlayer(p);
                p.setHealth(20);
                p.setFoodLevel(20);
                // Clear inventory
                p.getInventory().clear();
                // Give 3 steaks
                p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 3));
                // Clear xp and all levels
                p.setLevel(0);
                p.setExp(0);
                p.setTotalExperience(0);
                // Restore hunger
                p.setSaturation(20);
                // Play a sound
                Bukkit.getScheduler().runTask(MonadUHC.instance(), () -> {
                    p.teleport(MonadUHC.instance().getLocationManager().getScatterLocation(p.getWorld(), 0, 0, 500));
                    p.setGameMode(GameMode.SURVIVAL);
                    p.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(20, 1));
                });
                p.playSound(p.getLocation(), "minecraft:entity.player.levelup", 1, 1);
            });
            // Temporarily register a No Damage Listener
        });

        game.setGameLoopActions(actions);

        // TODO: IMPROVE THIS SUCH THAT IT IS UNIT TESTABLE
        Bukkit.getScheduler().runTaskTimerAsynchronously(MonadUHC.instance(), this.game, 0, 20L);
    }

    /**
     * @return the current game object.
     */
    public Game game() {
        return this.game;
    }

    /**
     * Sets the current game object.
     * 
     * @param game the game object.
     * 
     * @return the new game object.
     */
    public Game game(final Game game) {
        // log last game.
        this.logGame(this.game);
        // set new game.
        this.game = game;
        // return new game.
        return this.game();
    }

    /**
     * Helper function to handle the current -> next second transition and update
     * everything
     * 
     * @param game
     */
    public void gameLoopUpdates(final Game game) {

    }

    /**
     * Logs a game object to file.
     * 
     * @param game the game object.
     */
    public void logGame(Game game) {
        // TODO: serialize the game object to json and save it to a file.
        log.info("Last Game was: " + game);
    }

}
