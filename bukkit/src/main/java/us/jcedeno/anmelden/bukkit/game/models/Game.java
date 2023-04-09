package us.jcedeno.anmelden.bukkit.game.models;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor(staticName = "of")
@AllArgsConstructor
@Log4j2
public class Game implements Runnable {
    // Add a GAME ID, and use flat file to auto
    private final Long startTime = System.currentTimeMillis();
    private @NonNull Integer currentSecond = 0;

    private @Getter @Setter @NonNull Map<Integer, Consumer<Game>> gameLoopActions = new HashMap<>();

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
        if (this.holding){
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

    /*
     * All the actions that need to happen at seconds n+1. That is, each actual
     * second of the game. e.g.:
     * - At second 60 heal all players.
     * - At second 600 enable pvp
     */
    protected void gameLoopActions() {
        log.info("Things that need to happened at second seconds t+1. Current second: " + this.currentSecond);
        if (this.isGameOver()) {
            log.info("Tried to run game loop actions, but the game is over.");
            return;
        }
        var currentTickTasks = this.gameLoopActions.get(currentSecond);

        if (currentTickTasks != null) {
            currentTickTasks.accept(this);
            return;
        }
        System.out.println("No actions for current second: " + this.currentSecond);
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
