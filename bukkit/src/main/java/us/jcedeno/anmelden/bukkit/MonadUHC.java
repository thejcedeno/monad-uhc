package us.jcedeno.anmelden.bukkit;

import java.util.function.Function;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import cloud.commandframework.annotations.AnnotationParser;
import cloud.commandframework.arguments.parser.ParserParameters;
import cloud.commandframework.arguments.parser.StandardParameters;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import lombok.Getter;
import us.jcedeno.anmelden.bukkit._utils.fastinv.FastInvManager;
import us.jcedeno.anmelden.bukkit.scenarios.ScenarioManager;
import us.jcedeno.anmelden.bukkit.teams.TeamManager;

/**
 * The entry point of the Monad UHC plugin.
 * 
 * @author jcedeno and his beautiful gf.
 */
public class MonadUHC extends JavaPlugin {
    protected static MonadUHC instance;
    /** Command Manager for cloud framework */
    private @Getter PaperCommandManager<CommandSender> paperCommandManager;
    private @Getter AnnotationParser<CommandSender> annotationParser;
    /** Managers */
    private @Getter ScenarioManager scenarioManager;
    private @Getter TeamManager teamManager;

    /**
     * @return The instance of the MonadUHC plugin.
     */
    public static MonadUHC instance() {
        return MonadUHC.instance;
    }

    @Override
    public void onEnable() {
        /** Set the plugin instance to the current. */
        MonadUHC.instance = this;
        /** Register FastInv */
        FastInvManager.register(this);
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
        this.teamManager = new TeamManager();

        constructCommands();
    }

    /**
     * A method that constructs all the cloud framework commands.
     */
    private void constructCommands() {
        // Parse all @CommandMethod-annotated methods
        this.annotationParser.parse(this);
        // Parse all @CommandContainer-annotated classes
        try {
            this.annotationParser.parseContainers();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}
