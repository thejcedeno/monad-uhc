package us.jcedeno.anmelden.bukkit.scenarios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit.scenarios.impl.Cutclean;
import us.jcedeno.anmelden.bukkit.scenarios.models.BaseScenario;
import us.jcedeno.anmelden.bukkit.scenarios.models.ListenerScenario;

/**
 * A Singleton class that handles all the scenarios and their registration.
 * 
 * @author thejcedeno
 */
public class ScenarioManager {
    private Map<BaseScenario, Boolean> scenarios = new HashMap<>() {
        {
            put(Cutclean.create(), false);
        }
    };

    /**
     * Constructor for the ScenarioManager class.
     * 
     * @param instance The instance of the plugin.
     */
    public ScenarioManager(final MonadUHC instance) {
        this.registerScenarios();
    }

    /**
     * A utility function to get all the enabled scenarios.
     * 
     * @return A list of all the enabled scenarios.
     */
    public List<BaseScenario> enabledScenarios() {
        return scenarios.entrySet().stream().filter(e -> e.getValue()).map(e -> e.getKey())
                .collect(Collectors.toList());
    }

    /**
     * A utility function to get all the scenarios in a map.
     * 
     * @return A map of all the scenarios.
     */
    public Map<String, BaseScenario> getScenariosMap() {
        return scenarios.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().name(), e -> e.getKey()));
    }

    /**
     * Function that handles all the scenario registration to make the gamemode
     * modifiers available.
     */
    protected void registerScenarios() {
        this.scenarios.keySet().forEach(BaseScenario::init);
    }

    /**
     * A utility function to enable a scenario by name.
     * *
     * 
     * @param scenarioName The name of the scenario to enable.
     * @throws RuntimeException If the scenario is not found.
     */
    public void enableScenario(String scenarioName) {
        var scenario = scenarios.keySet().stream().filter(s -> s.name().equalsIgnoreCase(scenarioName)).findFirst()
                .orElseThrow(() -> new RuntimeException("Scenario not found"));

        enableScenario(scenario);

    }

    protected void enableScenario(BaseScenario scenario) {
        var clazz = scenario.getClass();
        // Check if clazz implements org.bukkit.event.Listener
        if (Listener.class.isAssignableFrom(clazz))
            Bukkit.getPluginManager().registerEvents((ListenerScenario) scenario, MonadUHC.instance());

        scenario.enable();
        scenarios.put(scenario, true);
    }

    /**
     * A utility function to disable a scenario by name.
     * 
     * @param scenarioName The name of the scenario to disable.
     * @throws RuntimeException If the scenario is not found.
     */
    public void disableScenario(String scenarioName) {
        var scenario = scenarios.keySet().stream().filter(s -> s.name().equalsIgnoreCase(scenarioName)).findFirst()
                .orElseThrow(() -> new RuntimeException("Scenario not found"));

        disableScenario(scenario);
    }

    public void disableScenario(BaseScenario scenario) {
        var clazz = scenario.getClass();
        // Check if clazz implements org.bukkit.event.Listener
        if (Listener.class.isAssignableFrom(clazz))
            HandlerList.unregisterAll((ListenerScenario) scenario);

        scenario.disable();
        scenarios.put(scenario, false);
    }

}
