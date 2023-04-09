package us.jcedeno.anmelden.bukkit.game;

import java.util.Map;
import java.util.function.Consumer;

import us.jcedeno.anmelden.bukkit.game.models.Game;

public class GameTests {
    public static void main(String[] args) {

        var game = Game.of();
        game.setPvpTime(30);
        game.setFinalHealTime(15);

        Map<Integer, Consumer<Game>> actions = Map.of(0, (c) -> System.out.println("Game has begun!"),
                5, (c) -> System.out.println("Second 5 uwu."),
                game.finalHealTime(), (c) -> System.out.println("Final heal!"),
                game.pvpTime(), (c) -> System.out.println("PVP enabled!"),
                game.pvpTime() + 1, (c) -> {
                    System.out.println("Game ending!");
                    c.setActuallyEnded(true);
                }
                );

        game.setGameLoopActions(actions);
        game.setHolding(false);

        while (!game.isActuallyEnded()) {
            game.run();

            try {
                Thread.sleep(1000);
                System.out.println("tick");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

}
