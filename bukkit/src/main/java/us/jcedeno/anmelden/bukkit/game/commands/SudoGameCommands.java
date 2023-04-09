package us.jcedeno.anmelden.bukkit.game.commands;

import org.bukkit.command.CommandSender;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.ProxiedBy;
import cloud.commandframework.annotations.processing.CommandContainer;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.game.GameManager;

@CommandContainer
public class SudoGameCommands {
    private final GameManager gameManager = MonadUHC.instance().getGameManager();

    @ProxiedBy("start")
    @CommandMethod("sgame start [seconds]")
    @CommandDescription("Starts a new game instance, replacing the current one if existant.")
    private void startGame(final CommandSender sender,
            final @Argument(value = "seconds", defaultValue = "3") Integer seconds) {

    }

    /**
     * required by cloud.
     * 
     * @param parser the parser
     */
    public SudoGameCommands(AnnotationParser<CommandSender> parser) {
    }

}
