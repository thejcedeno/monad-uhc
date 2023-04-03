package us.jcedeno.anmelden.bukkit.scenarios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit._utils.GlobalUtils;
import us.jcedeno.anmelden.bukkit.scenarios.impl.Cutclean;
import us.jcedeno.anmelden.bukkit.scenarios.models.BaseScenario;

/**
 * A Singleton class that handles all the scenarios and their registration.
 * 
 * @author thejcedeno
 */
@Log4j2
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
    @SneakyThrows
    public ScenarioManager(final MonadUHC instance) {
        this.registerScenarios();

        String packageName = this.getClass().getPackageName() + ".impl";
        log.info("Package name: " + packageName);

        GlobalUtils.getClassesFromPackage(packageName).stream()
                .forEach(sc -> log.info("Scenario name is " + sc.getName()));

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
     * A utility function to check if a scenario is enabled.
     * 
     * @param scenario The scenario to check.
     * @return True if the scenario is enabled, false otherwise.
     */
    public boolean scenarioEnabled(BaseScenario scenario) {
        return scenarios.get(scenario);
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
     * A utility function to get a scenario from a string.
     * 
     * @param scenarioName The name of the scenario to get.
     */
    public BaseScenario getScenarioFromStr(String scenarioName) {
        return scenarios.keySet().stream().filter(s -> s.name().equalsIgnoreCase(scenarioName)).findFirst()
                .orElseThrow(() -> new RuntimeException("Scenario not found"));
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
    public boolean enableScenario(String scenarioName) {
        var scenario = scenarios.keySet().stream().filter(s -> s.name().equalsIgnoreCase(scenarioName)).findFirst()
                .orElseThrow(() -> new RuntimeException("Scenario not found"));

        return enableScenario(scenario);
    }

    public boolean enableScenario(BaseScenario scenario) {
        // Throw an exception if the scenario is already enabled.
        if (scenarios.get(scenario))
            throw new RuntimeException("Scenario already enabled");

        if (scenario instanceof Listener listener)
            Bukkit.getPluginManager().registerEvents(listener, MonadUHC.instance());

        scenario.enable();
        return scenarios.put(scenario, true);
    }

    /**
     * A utility function to disable a scenario by name.
     * 
     * @param scenarioName The name of the scenario to disable.
     * @throws RuntimeException If the scenario is not found.
     */
    public boolean disableScenario(String scenarioName) {
        var scenario = scenarios.keySet().stream().filter(s -> s.name().equalsIgnoreCase(scenarioName)).findFirst()
                .orElseThrow(() -> new RuntimeException("Scenario not found"));

        return disableScenario(scenario);
    }

    public boolean disableScenario(BaseScenario scenario) {
        // Throw an exception if the scenario is not enabled.
        var scenarioStatus = scenarios.get(scenario);
        if (!scenarioStatus) {
            log.info("Scenario not enabled. " + scenario.name() + ", " + scenarioStatus);
            throw new RuntimeException("Scenario not enabled");
        } else {
            log.info("Scenario enabled. " + scenario.name() + ", " + scenarioStatus);
        }

        if (scenario instanceof Listener listener)
            HandlerList.unregisterAll(listener);

        scenario.disable();
        return scenarios.put(scenario, false);
    }

}
