package us.jcedeno.anmelden.bukkit.scenarios.models;

import lombok.AllArgsConstructor;
import us.jcedeno.anmelden.bukkit.scenarios.ScenarioManager;

/**
 * A base class for all the scenarios.
 * 
 * This class is used to make sure all the scenarios have the same methods and
 * properties.
 * 
 * @author thejcedeno
 */
@AllArgsConstructor
public class BaseScenario implements IScenario {
    private String name;
    private String description;

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public void init() {
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    public void enable() {
        throw new UnsupportedOperationException("Unimplemented method 'onEnable'");
    }

    public void disable() {
        throw new UnsupportedOperationException("Unimplemented method 'onDisable'");
    }

    /**
     * A utility function to toggle the scenario using an instantiate scenario
     * manager. Ideally, this pattern will be implemented as an interface and will
     * be refactored out of this base object, but for now, it'll stay here
     * 
     * @param scenarioManager The scenario manager to use.
     * @return True if the scenario was enabled, false otherwise.
     */
    public boolean toggle(final ScenarioManager scenarioManager) {
        return scenarioManager.scenarioEnabled(this) ? scenarioManager.disableScenario(this)
                : scenarioManager.enableScenario(this);
    }

}
