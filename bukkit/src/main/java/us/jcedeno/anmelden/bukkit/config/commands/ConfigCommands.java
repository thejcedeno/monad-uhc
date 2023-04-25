package us.jcedeno.anmelden.bukkit.config.commands;

import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.ProxiedBy;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.config.models.Rule;

/**
 * A class that contains all the commands for the scenario.
 * 
 * @author thejcedeno
 */
@CommandContainer
@Log4j2
public class ConfigCommands {

    /**
     * Returns all currently registered rules to the sender.
     * 
     * @param sender the command sender.
     */
    @CommandMethod("config list")
    public void listRules(final CommandSender sender) {
        log.info("Listing all rules.");
        // Get all rules
        final var rules = MonadUHC.instance().getConfigManager().rules();
        // Send pretty message to user
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                String.format("<Green>Currently registered rules: <white><bold>%s</bold></white>",
                        String.join(", ", rules.stream().map(Rule::name).collect(Collectors.toList())))));
    }

    /**
     * Returns all registered and active rules.
     * 
     * @param sender
     */
    @CommandMethod("config")
    public void listActiveRules(final CommandSender sender) {
        log.info("Listing all active rules.");
        // Get all rules
        final List<Rule> rules = MonadUHC.instance().getConfigManager().rulesMap().entrySet()
                .stream().filter(Entry::getValue).map(Entry::getKey)
                .collect(Collectors.toList());
        // Send pretty message to user
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                String.format("<Green>Currently active rules: <white><bold>%s</bold></white>",
                        String.join(", ", rules.stream().map(Rule::name).collect(Collectors.toList())))));
    }

    /**
     * Enables a rule.
     * 
     * @param sender the command sender.
     * @param rule   the rule to enable.
     */
    @CommandMethod("config enable <rule>")
    public void enableRule(final CommandSender sender, final @Argument(value = "rule") String rule) {
        log.info("Enabling rule: {}", rule);
        // Enable rule
        MonadUHC.instance().getConfigManager().enableRule(rule);
        // Send pretty message to user
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                String.format("<Green>Scenario <white><bold>%s</bold></white> has been enabled.", rule)));
    }

    /**
     * Disables a rule.
     * 
     * @param sender the command sender.
     * @param rule   the rule to disable.
     */
    @CommandMethod("config disable <rule>")
    public void disableRule(final CommandSender sender,
            final @Argument(value = "rule", suggestions = "rules") String rule) {
        log.info("Disabling rule: {}", rule);
        // Disable rule
        MonadUHC.instance().getConfigManager().disableRule(rule);
        // Send pretty message to user
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                String.format("<Green>Scenario <white><bold>%s</bold></white> has been disabled.", rule)));
    }

    /**
     * Toggles a rule.
     * 
     * @param sender the command sender.
     * @param rule   the rule to toggle.
     */
    @CommandMethod("config toggle <rule>")
    public void toggleRule(final CommandSender sender,
            final @Argument(value = "rule", suggestions = "rules") String rule) {
        log.info("Toggling rule: {}", rule);
        // Toggle rule
        MonadUHC.instance().getConfigManager().toggleRule(rule);
        // Send pretty message to user
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                String.format("<Green>Scenario <white><bold>%s</bold></white> has been toggled.", rule)));
    }

    @ProxiedBy("cfg")
    @CommandMethod("config get-all")
    public void configGetAll(final CommandSender sender) {
        log.info("Getting all config values.");
        // Get all config values
        var cfg = MonadUHC.instance().getConfigManager().config();
        // Send pretty message to user
        sender.sendMessage(MiniMessage.miniMessage().deserialize("<Green>Current config values: \n"
                + Stream.of(cfg.getClass().getFields()).filter(f -> !f.getName().contains("_")).map(field -> {
                    try {
                        return String.format("<white><bold>%s</bold></white>: <white><bold>%s</bold></white>",
                                field.getName(), field.get(cfg));
                    } catch (Exception e) {
                        return String.format("<Red>Failed to get config value <white><bold>%s</bold></white>.\n%s",
                                field.getName(), e.getMessage());
                    }
                }).collect(Collectors.joining("\n"))));
        // Send active rules.
        this.listActiveRules(sender);

    }

    @CommandMethod("config change <field> <value>")
    @SneakyThrows
    public void configChangeTimedEvent(final CommandSender sender, final @Argument("field") String field,
            final @Greedy @Argument("value") String value) {
        log.info("Changing timed event: {} to {}", field, value);
        // Change timed event
        var cfg = MonadUHC.instance().getConfigManager().config();

        var cfgField = cfg.getClass().getField("field");
        cfgField.trySetAccessible();
        /// Actually update the value
        try {
            cfgField.set(cfg, value);
        } catch (Exception e) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                    String.format(
                            "<Red>Failed to change timed event <white><bold>%s</bold></white> to <white><bold>%s</bold></white>.\n%s",
                            field, value, e.getMessage())));
            e.printStackTrace();
            return;
        }
        // Send pretty message to user
        sender.sendMessage(MiniMessage.miniMessage().deserialize(
                String.format(
                        "<Green>Timed event <white><bold>%s</bold></white> has been changed to <white><bold>%s</bold></white>.",
                        field, value)));
    }

    /**
     * A function used by cloud to register command completions from the scenarios
     * argument.
     */
    @Suggestions("rules")
    public @NonNull List<String> getRegisteredRules(
            final @NonNull CommandContext<CommandSender> ctx,
            final @NonNull String input) {
        return MonadUHC.instance().getConfigManager().rules().stream().map(r -> r.getClass().getSimpleName())
                .collect(Collectors.toList());
    }

    /**
     * Creates a new ScenarioCommands instance. CLOUD Framework magic.
     * 
     * @param parser the annotation parser.
     */
    public ConfigCommands(final AnnotationParser<CommandSender> parser) {
    }

}
