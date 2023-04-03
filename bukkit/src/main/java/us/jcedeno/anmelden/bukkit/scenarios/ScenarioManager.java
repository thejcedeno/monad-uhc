package us.jcedeno.anmelden.bukkit.scenarios;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import lombok.extern.log4j.Log4j2;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit._utils.GlobalUtils;
import us.jcedeno.anmelden.bukkit.scenarios.annotations.Scenario;
import us.jcedeno.anmelden.bukkit.scenarios.models.BaseScenario;

/**
 * A Singleton class that handles all the scenarios and their registration.
 * 
 * @author thejcedeno
 */
@Log4j2
public class ScenarioManager {
    private Map<BaseScenario, Boolean> scenarios = new HashMap<>();

    /**
     * Constructor for the ScenarioManager class.
     * 
     * @param instance The instance of the plugin.
     */
    public ScenarioManager(final MonadUHC instance) {
        /**
         * The following might look like black magic, but I promise it isn't. It's just
         * reflections and annotation processing.
         * 
         * Basically, this code allows other devs to create and register new scenarios
         * by creating a class under the scenarios/impl folder that extends BaseScenario
         * and annotating it with @Scenario.
         * 
         * TODO: Refactor this clusterf of a method
         */
        GlobalUtils.findAnnotatedClasses(this.getClass().getPackageName() + ".impl", Scenario.class).stream()
                .forEach(sc -> {
                    // Check if class is of base scenario type.
                    if (!BaseScenario.class.isAssignableFrom(sc)) {
                        log.warn(String.format("Class %s is not a subclass of BaseScenario.", sc.getName()));
                        return;
                    }
                    // Check if class has Scenario annotation
                    var annotation = sc.getAnnotation(Scenario.class);

                    if (annotation != null) {
                        log.info(String.format("Attempting to register scenario from class %s.", sc.getName()));

                        /**
                         * Get all the class's constructors to check if there is a constructor that
                         * takes 2 strings (name, description).
                         * 
                         * TODO: Change this to 3 strings (name, description, ui-material)
                         */
                        var constructors = sc.getDeclaredConstructors();
                        // Add a label to the for loop so we can break out of it.
                        out: for (var con : constructors) {
                            var params = con.getParameters();
                            // Params length can only be 2 for name and decription.
                            if (params.length != 2)
                                continue out;

                            // If the type of the current constructor isn't String, then exit early.
                            for (var param : params)
                                if (param.getType() != String.class)
                                    continue out;

                            // Invoke the constructor and register the scenario with the scenario manager.
                            try {
                                var scenario = (BaseScenario) con.newInstance(annotation.name(),
                                        annotation.description());
                                // Add it to the scenarios hashset as disabled.
                                // TODO: Make the init method do this automatically?
                                scenarios.put(scenario, false);
                                // Log the registration.
                                log.info(String.format("Registered scenario {\nname=%s, \ndescription=%s, \nui=%s\n}.",
                                        annotation.name(), annotation.description(), annotation.ui()));
                            } catch (Exception e) {
                                log.error(
                                        String.format(
                                                "Failed to register scenario {\nname=%s, \ndescription=%s, \nui=%s\n}.",
                                                annotation.name(), annotation.description(), annotation.ui()));
                                e.printStackTrace();
                            }
                        }

                    }
                });

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

    /**
     * A utility function to enable a scenario.
     * 
     * @param scenario The scenario to enable.
     * @throws RuntimeException If the scenario is already enabled.
     * 
     * @return True if the scenario was enabled, false otherwise.
     */
    public boolean enableScenario(BaseScenario scenario) {
        // Throw an exception if the scenario is already enabled.
        if (scenarios.get(scenario))
            throw new RuntimeException("Scenario already enabled");

        scenario.enable();

        if (scenario instanceof Listener listener)
            Bukkit.getPluginManager().registerEvents(listener, MonadUHC.instance());

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
        }

        scenario.disable();

        if (scenario instanceof Listener listener)
            HandlerList.unregisterAll(listener);

        log.info("Scenario enabled. " + scenario.name() + ", " + scenarioStatus);

        return scenarios.put(scenario, false);
    }

}
