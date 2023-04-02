package us.jcedeno.anmelden.bukkit.scenarios.commands;

import static net.kyori.adventure.text.minimessage.MiniMessage.miniMessage;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.annotations.Argument;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import cloud.commandframework.annotations.ProxiedBy;
import cloud.commandframework.annotations.processing.CommandContainer;
import cloud.commandframework.annotations.specifier.Greedy;
import cloud.commandframework.annotations.specifier.Liberal;
import cloud.commandframework.annotations.specifier.Quoted;
import lombok.extern.log4j.Log4j2;
import us.jcedeno.anmelden.bukkit.MonadUHC;

/**
 * A class holding all the user-facing Scenario commands.
 * 
 * @author thejcedeno.
 */
@CommandContainer
@Log4j2
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

    @CommandPermission("anmelden.scenarios.admin")
    @CommandMethod("scenario menu")
    public void scenario(final @NonNull CommandSender sender) {
        sender.sendMessage(String.format("Current Server Time: %s", System.currentTimeMillis()));

    }

    @ProxiedBy("echo")
    @CommandMethod("scenario echo <echo-text>")
    public void echoToUser(final @NonNull CommandSender sender, @Argument("echo-text") @Greedy String text) {
        if (sender instanceof Player p) {
            p.sendMessage(miniMessage().deserialize(text));
            return;
        }
        sender.sendMessage(text);
    }

    @ProxiedBy("echo-all")
    @CommandMethod("scenario echo-all <text> [parse]")
    public void echoToAll(final @NonNull CommandSender sender, @Argument("text") @Quoted String text,
            @Argument(value = "parse", defaultValue = "true") @Liberal Boolean parse) {

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (parse) {
                player.sendMessage(miniMessage().deserialize(text));
                return;
            }
            player.sendMessage(text);
        });

        log.info("Echoed to all players the message: " + text + " with parse: " + parse);

    }

    // REQUIRED BY CLOUD FRAMEWORK
    public ScenarioCommands(final @NonNull AnnotationParser<CommandSender> annotationParser) {
    }

}
