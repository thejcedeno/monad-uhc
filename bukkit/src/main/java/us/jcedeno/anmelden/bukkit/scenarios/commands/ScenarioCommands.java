package us.jcedeno.anmelden.bukkit.scenarios.commands;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.processing.CommandContainer;
import us.jcedeno.anmelden.bukkit.MonadUHC;

/**
 * A class holding all the user-facing Scenario commands.
 * 
 * @author thejcedeno.
 */
@CommandContainer
public final class ScenarioCommands {

    @CommandMethod("scenario")
    public void enabledScenarios(final @NonNull CommandSender sender) {
        // Sender error message, in red, saying that there are no scenarios enabled.
        if (MonadUHC.instance().getScenarioManager().enabledScenarios().isEmpty()) {
            sender.sendMessage(miniMessage().deserialize("<red>There are no scenarios enabled."));
            return;
        }
        sender.sendMessage(miniMessage().deserialize("<green>Enabled Scenarios:"));

        MonadUHC.instance().getScenarioManager().enabledScenarios()
                .forEach(scenario -> sender.sendMessage("- " + scenario.name()));

    }

    // REQUIRED BY CLOUD FRAMEWORK
    public ScenarioCommands(final @NonNull AnnotationParser<CommandSender> annotationParser) {
    }

}
