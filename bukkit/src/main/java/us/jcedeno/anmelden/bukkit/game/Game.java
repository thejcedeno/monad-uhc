package us.jcedeno.anmelden.bukkit.game;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
public class Game {
    // Add a GAME ID, and use flat file to auto 
    private final Long startTime = System.currentTimeMillis();
    private @NonNull Integer currentSecond = 0;

    // Timings
    private @NonNull Integer finalHealTime = 10;
    private @NonNull Integer pvpTime = 60 * 10;

    public int tick() {
        return ++currentSecond;
    }

    public int currentSecond() {
        return currentSecond;
    }

    public Long startTime() {
        return startTime;
    }

}
