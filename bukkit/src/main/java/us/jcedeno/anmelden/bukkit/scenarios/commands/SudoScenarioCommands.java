package us.jcedeno.anmelden.bukkit.scenarios.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.anmelden.bukkit.MonadUHC;

/**
 * A class holding all the super-user, moderator Scenario commands.
 * 
 * @author thejcedeno.
 */
@CommandContainer
@Log4j2
public class SudoScenarioCommands {
    /**
     * Enables a scenario.
     * 
     * @param sender   the command sender.
     * @param scenario the scenario to enable.
     */
    @CommandPermission("anmelden.scenarios.admin")
    @CommandMethod("sscenario enable <scenario>")
    public void enableScenario(final CommandSender sender,
            @Argument(value = "scenario", suggestions = "scenarios") final String scenario) {
        log.debug("Enabling scenario: {}", scenario);
        MonadUHC.instance().getScenarioManager().enableScenario(scenario.toString());
        // Send pretty message to user
        if (sender instanceof Player p) {
            p.sendMessage(MiniMessage.miniMessage().deserialize(
                    String.format("<Green>Scenario <white><bold>%s</bold></white> has been enabled.", scenario)));
        } else {
            sender.sendMessage(String.format("Scenario %s has been enabled.", scenario));
        }

    }

    /**
     * Disables a scenario.
     * 
     * @param sender   the command sender.
     * @param scenario the scenario to disable.
     */
    @CommandPermission("anmelden.scenarios.admin")
    @CommandMethod("sscenario disable <scenario>")
    public void disableScenario(final CommandSender sender,
            @Argument(value = "scenario", suggestions = "scenarios") final String scenario) {
        log.debug("Disabling scenario: {}", scenario);
        MonadUHC.instance().getScenarioManager().disableScenario(scenario.toString());
        // Send pretty message to user
        if (sender instanceof Player p) {
            p.sendMessage(MiniMessage.miniMessage().deserialize(
                    String.format("<Red>Scenario <white><bold>%s</bold></white> has been disabled.", scenario)));
        } else {
            sender.sendMessage(String.format("Scenario %s has been disabled.", scenario));
        }

    }

    /**
     * Toggles a scenario.
     * @param sender the command sender.
     * @param scenario the scenario to toggle.
     */
    @CommandPermission("anmelden.scenarios.admin")
    @CommandMethod("sscenario toggle <scenario>")
    public void toggleScenario(final CommandSender sender,
            @Argument(value = "scenario", suggestions = "scenarios") final String scenario) {
        log.debug("Toggling scenario: {}", scenario);
        var scenarioManager = MonadUHC.instance().getScenarioManager();

        var toggled = !scenarioManager.getScenarioFromStr(scenario.toString()).toggle(scenarioManager);
        log.info("Scenario {} has been toggled to {}", scenario, toggled);

        // Send pretty message to user
        if (sender instanceof Player p) {
            p.sendMessage(MiniMessage.miniMessage().deserialize(
                    String.format("<yellow><white><bold>%s</bold></white> has been <bold>%s.</bold>", scenario,
                            toggled ? "<green>enabled</green>" : "<red>disabled</red>")));
        } else {
            sender.sendMessage(String.format("Scenario %s has been", scenario));
        }

    }

    /**
     * Needed for the command framework to work.
     * 
     * @param parser the parser.
     */
    public SudoScenarioCommands(AnnotationParser<CommandSender> parser) {
        log.debug("At this point, cloud injects the command.");
    }

    /**
     * A function used by cloud to register command completions from the scenarios
     * argument.
     */
    @Suggestions("scenarios")
    public @NonNull List<String> getRegisteredScenarios(
            final @NonNull CommandContext<CommandSender> ctx,
            final @NonNull String input) {
        return MonadUHC.instance().getScenarioManager().getScenariosMap().entrySet().stream()
                .map((entry) -> entry.getValue().name()).collect(Collectors.toList());
    }
}
