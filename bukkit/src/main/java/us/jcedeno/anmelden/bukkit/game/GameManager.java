package us.jcedeno.anmelden.bukkit.game;

import java.util.HashMap;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.game.listener.LobbyStage;
import us.jcedeno.anmelden.bukkit.game.models.Game;
import us.jcedeno.anmelden.bukkit.game.models.Stage;

/**
 * A class to manage all the interactions with the game object, the game loop
 * and the other systems.
 * 
 * @author jcedeno
 */
@Log4j2
public class GameManager implements Listener {

    protected Game game;

    private LobbyStage lobbyListener;

    public GameManager() {
        this.game = Game.of();
        this.tempGameLoop();
        // Update game stage and register lobby listeners
        this.lobbyListener = new LobbyStage();
        this.game().setStage(Stage.LOBBY);
        Bukkit.getPluginManager().registerEvents(lobbyListener, MonadUHC.instance());

        // TODO: IMPROVE THIS <-- GAME LOOP
        Bukkit.getScheduler().runTaskTimerAsynchronously(MonadUHC.instance(), this.game, 0, 20L);
    }

    /**
     * @return the current game object.
     */
    public Game game() {
        return this.game;
    }

    /**
     * Function that updates the game stage and registers the appropriate listeners.
     */
    public void registerStage(Stage stage) {
        switch (stage) {
            case LOBBY:
                Bukkit.getPluginManager().registerEvents(new LobbyStage(), MonadUHC.instance());
                break;
            case GAME:
            case STARTING:
                HandlerList.unregisterAll(lobbyListener);
                break;
            case END:
                break;
            default:
                break;
        }

        this.game.setStage(stage);
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

    private void tempGameLoop() {
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
            Bukkit.getOnlinePlayers().stream()
                    .forEach(p -> p.getInventory().addItem(new ItemStack(Material.COOKED_BEEF, 3)));
        });

        game.setGameLoopActions(actions);
    }

    private void sendPrettyMsg(String msg) {
        Bukkit.broadcast(MiniMessage.miniMessage().deserialize(msg));
    }

    /**
     * A function that starts the scattering process, waits until completion, then
     * properly starts the game
     */
    public void startGame() {        
        registerStage(Stage.STARTING);

        // Prepare scatter locations
        sendPrettyMsg("<rainbow>Calculating scatter locations...</rainbow>");

        var locManager = MonadUHC.instance().getLocationManager();
        var scatterWorld = locManager.getGameWorld();

        var locs = Bukkit.getOnlinePlayers().stream().map(p -> locManager.getScatterLocation(scatterWorld, 0, 0, 1000))
                .toList();

        sendPrettyMsg("<gold><bold>Starting scatter ");

        var players = Bukkit.getOnlinePlayers().iterator();

        locs.forEach(l -> {
            if (players.hasNext()) {
                var p = players.next();
                p.setGameMode(GameMode.SURVIVAL);
                p.teleport(l);
                p.sendMessage(MiniMessage.miniMessage().deserialize("<gray>You've been teleported!"));
                MonadUHC.instance().getPlayerManager().registerGamePlayer(p);
                p.setHealth(20);
                p.setFoodLevel(20);
                // Clear inventory
                p.getInventory().clear();
                // Give 3 steaks
                // Clear xp and all levels
                p.setLevel(0);
                p.setExp(0);
                p.setTotalExperience(0);
                // Restore hunger
                p.setSaturation(20);

            }
        });

        sendPrettyMsg("<green>Scatter completed");

        broadcastRules();

        new BukkitRunnable() {
            int countdown = 5;

            @Override
            public void run() {
                if (countdown <= 0) {
                    this.cancel();
                    actuallyStartTheGame();
                    return;
                }
                sendPrettyMsg(String.format("<yellow>Starting in <white>%s</white> seconds.</yellow>", countdown));
                countdown--;
            }

        }.runTaskTimer(MonadUHC.instance(), 0, 20L);

    }

    private void actuallyStartTheGame() {
        this.game.setHolding(false);

    }

    private void broadcastRules() {
        sendPrettyMsg("1. Don't be evil.");
        sendPrettyMsg("2. Have fun.");

    }

}
