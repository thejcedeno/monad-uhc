package us.jcedeno.anmelden.bukkit.config.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.processing.CommandContainer;
import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.anmelden.bukkit.MonadUHC;

/**
 * A class that contains all the commands for the sudo scenario.
 * 
 * @author thejcedeno
 */
@CommandContainer
@Log4j2
public class SudoConfigCommands {

    /**
     * Enables a rule.
     * 
     * @param sender the command sender.
     * @param rule   the rule to enable.
     */
    @CommandPermission("anmelden.scenarios.admin")
    // @ProxiedBy("rule <rule>")
    // Proxied By can only be used with literals. If we want to be able to parse
    // this a a literal, we need a way to dynamically register a literal that holds
    // the current registered rules as static
    @CommandMethod("srule enable <rule>")
    public void enableRule(final CommandSender sender,
            @Argument(value = "rule", suggestions = "rules") final String rule) {
        log.debug("Enabling rule: {}", rule);
        MonadUHC.instance().getConfigManager().enableRule(rule.toString());
        // Send pretty message to user
        if (sender instanceof Player p) {
            p.sendMessage(MiniMessage.miniMessage().deserialize(
                    String.format("<Green>Rule <white><bold>%s</bold></white> has been enabled.", rule)));
        } else {
            sender.sendMessage(String.format("Rule %s has been enabled.", rule));
        }

    }

    /**
     * Disables a rule.
     * 
     * @param sender the command sender.
     * @param rule   the rule to disable.
     */
    @CommandPermission("anmelden.scenarios.admin")
    @CommandMethod("srule disable <rule>")
    public void disableRule(final CommandSender sender,
            @Argument(value = "rule", suggestions = "rules") final String rule) {
        log.debug("Disabling rule: {}", rule);
        MonadUHC.instance().getConfigManager().disableRule(rule.toString());
        // Send pretty message to user
        if (sender instanceof Player p) {
            p.sendMessage(MiniMessage.miniMessage().deserialize(
                    String.format("<Red>Rule <white><bold>%s</bold></white> has been disabled.", rule)));
        } else {
            sender.sendMessage(String.format("Rule %s has been disabled.", rule));
        }

    }

    /**
     * Toggles a rule.
     * 
     * @param sender the command sender.
     * @param rule   the rule to toggle.
     */
    @CommandPermission("anmelden.scenarios.admin")
    @CommandMethod("srule toggle <rule>")
    public void toggleRule(final CommandSender sender,
            @Argument(value = "rule", suggestions = "rules") final String rule) {
        log.debug("Toggling rule: {}", rule);
        var ruleManager = MonadUHC.instance().getConfigManager();

        var toggled = !ruleManager.getRuleFromStr(rule.toString()).toggle(ruleManager);
        log.info("Rule {} has been toggled to {}", rule, toggled);

        // Send pretty message to user
        if (sender instanceof Player p) {
            p.sendMessage(MiniMessage.miniMessage().deserialize(
                    String.format("<yellow><white><bold>%s</bold></white> has been <bold>%s.</bold>", rule,
                            toggled ? "<green>enabled</green>" : "<red>disabled</red>")));
        } else {
            sender.sendMessage(String.format("Rule %s has been", rule));
        }

    }

    /**
     * Cloud framework magic.
     * 
     * @param parser the annotation parser.
     */
    public SudoConfigCommands(final AnnotationParser<CommandSender> parser) {
    }
}
