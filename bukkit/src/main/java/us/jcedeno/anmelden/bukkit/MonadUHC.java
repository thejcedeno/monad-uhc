package us.jcedeno.anmelden.bukkit;

import java.util.function.Function;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.Command;
import cloud.commandframework.Command.Builder;
import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.DoubleArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.arguments.standard.StringArgument.StringMode;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import us.jcedeno.anmelden.bukkit.scenarios.ScenarioManager;

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
 * @author jcedeno and her gf
 */
public class MonadUHC extends JavaPlugin {
    /** Command Manager for cloud framework */
    private @Getter PaperCommandManager<CommandSender> paperCommandManager;
    private @Getter AnnotationParser<CommandSender> annotationParser;
    /** Managers */
    private ScenarioManager scenarioManager;

    @Override
    public void onEnable() {
        /** Intialize the command manager. */
        try {
            this.paperCommandManager = new PaperCommandManager<>(this, CommandExecutionCoordinator.simpleCoordinator(),
                    Function.identity(),
                    Function.identity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        /** Register brigadier. */
        try {
            this.paperCommandManager.registerBrigadier();
            this.paperCommandManager.registerAsynchronousCompletions();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        /** Reigster annotation parser. */
        final Function<ParserParameters, CommandMeta> commandMetaFunction = p -> CommandMeta.simple()
                // This will allow you to decorate commands with descriptions
                .with(CommandMeta.DESCRIPTION, p.get(StandardParameters.DESCRIPTION, "No description"))
                .build();

        this.annotationParser = new AnnotationParser<>(this.paperCommandManager, CommandSender.class,
                commandMetaFunction);
        /** Register Game Related Managers */
        this.scenarioManager = new ScenarioManager(this);

        // Construct commands
        constructCommands();
    }

    private void constructCommands(){
         // Parse all @CommandMethod-annotated methods
        this.annotationParser.parse(this);
        // Parse all @CommandContainer-annotated classes
        try {
            this.annotationParser.parseContainers();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        System.out.println("bye bye");
    }

}
