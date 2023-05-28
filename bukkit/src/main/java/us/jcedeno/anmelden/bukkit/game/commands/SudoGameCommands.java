package us.jcedeno.anmelden.bukkit.game.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.ProxiedBy;
import cloud.commandframework.annotations.processing.CommandContainer;
import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.game.GameManager;
import us.jcedeno.anmelden.bukkit.game.models.Stage;

@CommandContainer
public class SudoGameCommands {
    private final GameManager gameManager = MonadUHC.instance().getGameManager();

    @ProxiedBy("start")
    @CommandMethod("sgame start [seconds]")
    @CommandDescription("Starts a new game instance, replacing the current one if existant.")
    public void startGame(final Player sender,
            final @Argument(value = "seconds", defaultValue = "3") Integer seconds) {

        gameManager.startGame();

        /**
        // Send game started message:
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<gradient:#ff0000:#00ff00>Game started!</gradient>"));

        gameManager.game().setHolding(false);

        gameManager.registerStage(Stage.STARTING);
        */

    }

    /**
     * required by cloud.
     * 
     * @param parser the parser
     */
    public SudoGameCommands(AnnotationParser<CommandSender> parser) {
    }

}
