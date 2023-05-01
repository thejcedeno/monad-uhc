package us.jcedeno.anmelden.bukkit.game.models;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.bukkit.Bukkit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.text.minimessage.MiniMessage;

@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Log4j2
public class Game implements Runnable {
    // Add a GAME ID, and use flat file to auto
    private final Long startTime = System.currentTimeMillis();
    private @NonNull Integer currentSecond = 0;

    private @Getter @Setter @NonNull Map<Integer, Consumer<Game>> gameLoopActions = new HashMap<>();

    // Game stage, starts at init.
    private @Getter @Setter Stage stage = Stage.INIT;

    // Timings
    private @Setter @NonNull Integer finalHealTime = 10;
    private @Setter @NonNull Integer pvpTime = 60 * 10;

    private @Getter @Setter boolean holding = true;

    private @Getter @Setter boolean hasFirstTickHappened = false;
    private @Getter @Setter boolean gameOver = false;
    private @Setter @Getter boolean actuallyEnded = false;

    @Override
    public void run() {
        if (this.actuallyEnded) {
            return;
        }
        if (this.holding) {
            return;
        }
        if (!this.hasFirstTickHappened) {
            this.hasFirstTickHappened = true;
            // Do all the "first tick actions"
            this.gameStartActions();
            // Return to hold off from t+1 game logic.
            return;
        }

        // Moves the game clock forward
        this.tick();
        this.gameLoopActions();
    }

    /**
     * Any domain specific actions that need to take place at second 0 of the game.
     * e.g.:
     * - Set the world border to the correct size.
     * - Set the world time to day.
     * - Set the world weather to clear.
     * - Set the world difficulty to normal.
     * - Heal all players.
     * - Feed all players.
     * - Display game has begun message
     */
    protected void gameStartActions() {
        log.info("Things that need to happened at second 0.");
        var currentTickTasks = this.gameLoopActions.get(0);

        if (currentTickTasks != null)
            currentTickTasks.accept(this);

    }

    // A function that takes int seconds and returns a properly formatted hh:mm:ss
    // string with hh hidden if 0
    public static String formatTime(int seconds) {
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        if (hours == 0) {
            return String.format("%02d:%02d", minutes, secs);
        } else {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        }
    }

    /*
     * All the actions that need to happen at seconds n+1. That is, each actual
     * second of the game. e.g.:
     * - At second 60 heal all players.
     * - At second 600 enable pvp
     */
    protected void gameLoopActions() {
        if (this.isGameOver()) {
            log.info("Tried to run game loop actions, but the game is over.");
            return;
        }
        var currentTickTasks = this.gameLoopActions.get(currentSecond);

        if (currentTickTasks != null) {
            currentTickTasks.accept(this);
            log.info("Ran actions for this tick.");
        } else {
            log.info("No actions for this tick.");
        }

        Bukkit.getOnlinePlayers().forEach(p -> p.sendActionBar(MiniMessage.miniMessage()
                .deserialize(String.format("<green>Time Elapsed: <white>%s", formatTime(currentSecond())))));

    }

    /**
     * Tick the game. Increaseas
     * 
     * @return the next second.
     */
    protected int tick() {
        return ++currentSecond;
    }

    /**
     * @return the current second.
     */
    public int currentSecond() {
        return currentSecond;
    }

    /**
     * @return the timestamp of when the game started.
     */
    public Long startTime() {
        return startTime;
    }

    /**
     * @return the time in seconds for the final heal.
     */
    public Integer finalHealTime() {
        return finalHealTime;
    }

    /**
     * @return the time in seconds for pvp to be enabled.
     */
    public Integer pvpTime() {
        return pvpTime;
    }

}
