package us.jcedeno.anmelden.bukkit.scenarios;

import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.scenarios.commands.ScenarioCommands;

public class ScenarioManager {
    private ScenarioCommands scenarioCommands;

    public ScenarioManager(final MonadUHC instance) {
        this.scenarioCommands = new ScenarioCommands(instance.getAnnotationParser());

    }

}
