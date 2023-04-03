package us.jcedeno.anmelden.bukkit.scenarios;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static Class<?>[] getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        var resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            var resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        var classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        var classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static void main(String[] args) {
        var packageName ="us.jcedeno.anmelden.bukkit.scenarios.impl";

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            var resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                var resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    var directory = new File(resource.toURI());
                    if (directory.exists()) {
                        var files = directory.listFiles();
                        for (var file : files) {
                            if (file.isFile() && file.getName().endsWith(".class")) {
                                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                                Class<?> clazz = Class.forName(className);

                                if(clazz.getAnnotations().length > 0)
                                    System.out.println("Scenarios with annotations: " + clazz.getName());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public static List<Class<?>> getClassesInPackage(String packageName) {
        log.info("Getting classes in package: " + packageName);
        List<Class<?>> classes = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            var resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                var resource = resources.nextElement();
                var file = new File(resource.getFile());
                if (file.isDirectory()) {
                    var files = file.listFiles();
                    for (var childFile : files) {
                        var childPath = packageName + "." + childFile.getName().replace(".class", "");
                        try {
                            classes.add(Class.forName(childPath));
                        } catch (ClassNotFoundException e) {
                            // Handle the exception as per your use case
                        }
                    }
                }
            }
        } catch (IOException e) {
            // Handle the exception as per your use case
            e.printStackTrace();
        }
        return classes;
    }

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

        // Using burningwave to find all the classes in the package.
        List<Class<?>> scenarios = List.of(getClasses(packageName));

        System.out.println("SCENARIOS: " + scenarios.size());

        scenarios.forEach(clz -> {
            log.info("FOUND SCENARIO Class: " + clz.getName());

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
