package us.jcedeno.anmelden.bukkit;

import java.util.function.Function;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.Command.Builder;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.DoubleArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.arguments.standard.StringArgument.StringMode;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import kr.entree.spigradle.annotations.SpigotPlugin;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * The entry point of the Bukkit side of the Anmelden Project.
 * 
 * <p>
 * <h3>Resources for development:</h3>
 * <ul>
 * 
 * <li>Minimessage viewer
 * <a href= "https://webui.adventure.kyori.net/">here<a/>.
 * </li>
 * 
 * <li>Minimessage Documentation
 * <a href= "https://docs.adventure.kyori.net/minimessage">here<a/>.
 * </li>
 * 
 * <li>Cloud Framework Documentation
 * <a href= "https://incendo.github.io/cloud/">here<a/>.
 * </li>
 * 
 * </ul>
 * </p>
 * 
 * <p>
 * For further details, contact develop at jcedeno@hynix.studio
 * </p>
 * 
 * @author jcedeno
 */
@SpigotPlugin
public class AnmeldenBukkit extends JavaPlugin {
    /** Command Manager for cloud framework */
    BukkitCommandManager<CommandSender> manager;
    /** Minimessage constant */
    private static final MiniMessage MM = MiniMessage.get();

    @Override
    public void onEnable() {
        /** Intialize the command manager. */
        try {
            this.manager = new PaperCommandManager<>(this, CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        /** Register brigadier. */
        this.manager.registerBrigadier();
        /** create new command using builder */
        Builder<CommandSender> builder = Command.newBuilder("cloud-command", CommandMeta.simple().build(),
                "cloudcommand", "cc");

        /** Using literals */
        builder = builder.literal("deploy", ArgumentDescription.of("The first agument in the command."), "create");
        /** Using String arguments */
        builder = builder.argument(StringArgument.of("Name for the game.", StringMode.QUOTED));
        /** Using numbers */
        builder = builder.argument(DoubleArgument.optional("Hours", 2));
        /** Upgraded hardware argument using liberal booleans */
        builder = builder.argument(BooleanArgument.optional("Upgraded Hardware", false));
        /** Handle the execution */
        builder = builder.handler(context -> {
            /** Send message to player */
            var sender = context.getSender();
            /** Name parameter */
            var name = context.get("Name for the game.").toString();
            /** Hours parameter */
            var hour = context.getOptional("Hours").isPresent() ? (double) context.get("Hours") : 2;
            /** Upgraded parameter */
            var upgraded = context.getOptional("Upgraded Hardware").isPresent()
                    ? (Boolean) context.getOptional("Upgraded Hardware").get()
                    : false;
            /** Send message to player */
            sender.sendMessage(MM.parse("<green>Hellow <yellow>" + sender.getName() + "<green>!\n"
                    + "<yellow>You have requested a game named <green>" + name + "<yellow> with <green>" + hour
                    + "<yellow> hours and <green>" + upgraded + "<yellow> upgraded hardware."));
        });

        /** register with manager. */
        manager.command(builder);
    }

    void changeSkin(Player player) {
        player.getPlayerProfile();
    }

    @Override
    public void onDisable() {
        System.out.println("bye bye");
    }

}
