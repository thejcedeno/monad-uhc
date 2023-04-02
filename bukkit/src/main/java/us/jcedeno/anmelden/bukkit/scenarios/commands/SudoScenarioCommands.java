package us.jcedeno.anmelden.bukkit.scenarios.commands;

import org.bukkit.command.CommandSender;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.specifier.Quoted;
import lombok.extern.log4j.Log4j2;
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
     * @param sender  the command sender.
     * @param scenario the scenario to enable.
     */
    @CommandPermission("anmelden.scenarios.admin")
    @CommandMethod("sscenario enable <scenario>")
    public void enableScenario(final CommandSender sender, @Argument("scenario") @Quoted final String scenario) {
        log.debug("Enabling scenario: {}", scenario);
        MonadUHC.instance().getScenarioManager().enableScenario(scenario);

    }

    /**
     * Needed for the command framework to work.
     * 
     * @param parser the parser.
     */
    public SudoScenarioCommands(AnnotationParser<CommandSender> parser) {
        log.debug("At this point, cloud injects the command.");
    }

}
