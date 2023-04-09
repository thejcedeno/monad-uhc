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
public class GameManager {

    protected Game game;
    protected NoDamageListener dNoDamageListener = new NoDamageListener();

    public static class NoDamageListener implements Listener {

        @EventHandler
        public void onPlayerDmgCancel(EntityDamageEvent e) {
            if (e instanceof Player p) {
                e.setCancelled(true);
            }
        }
    }

    public GameManager() {
        this.game = Game.of();
        game.setFinalHealTime(120);

        var actions = new HashMap<Integer, Consumer<Game>>();

        actions.put(15, c -> {
            HandlerList.unregisterAll(dNoDamageListener);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<gold>Player Damage has been enabled!"));
        });

        // What happens at second 0
        actions.put(0, g -> {
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("<gold>Game has begun!"));
            Bukkit.getOnlinePlayers().stream().forEach(p -> {
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
                Bukkit.getScheduler().runTask(MonadUHC.instance(), () -> p.setGameMode(GameMode.SURVIVAL));
                p.playSound(p.getLocation(), "minecraft:entity.player.levelup", 1, 1);
            });
            // Temporarily register a No Damage Listener
            Bukkit.getPluginManager().registerEvents(dNoDamageListener, MonadUHC.instance());
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
