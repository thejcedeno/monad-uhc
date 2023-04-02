package us.jcedeno.anmelden.bukkit.scenarios;

import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.scenarios.commands.ScenarioCommands;

public class ScenarioManager {
    private ScenarioCommands scenarioCommands;

    public ScenarioManager(final MonadUHC instance) {
        this.scenarioCommands = new ScenarioCommands(this);

    }

    /**
     * Function that handles all the scenario registration to make the gamemode
     * modifiers available.
     */
    protected void registerScenarios(){
        /*
         * TODO: Instead of doing this, figure out a way to register all the
         * scenariosautomatically using reflection and/or annotations. This will make it
         * so we don't have to manually register each scenario here.
         */
    }

}
