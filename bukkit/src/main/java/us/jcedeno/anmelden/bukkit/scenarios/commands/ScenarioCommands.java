package us.jcedeno.anmelden.bukkit.scenarios.commands;

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
import cloud.commandframework.annotations.specifier.Quoted;
import lombok.extern.log4j.Log4j2;
import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.anmelden.bukkit.scenarios.ScenarioManager;

@CommandContainer
@Log4j2
public final class ScenarioCommands {

    public ScenarioCommands(final @NonNull AnnotationParser<CommandSender> annotationParser) {
        // REQUIRED BY CLOUD FRAMEWORK
    }

    public ScenarioCommands(final ScenarioManager scenarioManager){
        log.info("Initializing Scenario Commands");
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
            p.sendMessage(MiniMessage.miniMessage().deserialize(text));
            return;
        }
        sender.sendMessage(text);
    }
    
    @ProxiedBy("echo-all")
    @CommandMethod("scenario echo-all <text> <parse>")
    public void echoToAll(final @NonNull CommandSender sender, @Argument("text") @Quoted String text, @Argument("parse")Boolean parse) {

        Bukkit.getOnlinePlayers().forEach(player -> {
            if (parse) {
                player.sendMessage(MiniMessage.miniMessage().deserialize(text));
                return;
            }
            player.sendMessage(text);
        });

        log.info("Echoed to all players the message: " + text + " with parse: " + parse);

    }


}
