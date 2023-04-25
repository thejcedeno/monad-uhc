package us.jcedeno.anmelden.bukkit.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import lombok.extern.log4j.Log4j2;
import us.jcedeno.anmelden.bukkit.MonadUHC;
import us.jcedeno.anmelden.bukkit._utils.GUtils;
import us.jcedeno.anmelden.bukkit.config.annotations.Setting;
import us.jcedeno.anmelden.bukkit.config.models.Rule;
import us.jcedeno.anmelden.bukkit.config.rules.NetherRule;

@Log4j2
public class ConfigManager {
    protected volatile Map<Rule, Boolean> rules = new ConcurrentHashMap<>();

    /**
     * Handle the auto initialization of the config manager.
     */
    public ConfigManager() {
        this.init();
    }

    /**
     * Initialize the config manager.
     */
    public void init() {
        GUtils.annotatedClasses(this.getClass().getPackageName() + ".rules", Setting.class)
                .stream().filter(Rule.class::isAssignableFrom).forEach(clz -> {
                    var annotation = clz.getAnnotation(Setting.class);
                    log.info(String.format("Attempting to register rule %s.", annotation.name()));

                    try {
                        var rule = (Rule) clz.getDeclaredConstructor(String.class, String.class)
                                .newInstance(annotation.name(), annotation.description());
                        this.rules.put(rule, false);
                    } catch (Exception e) {
                        log.error(String.format("Failed to register rule %s.", annotation.name()));
                        e.printStackTrace();
                    }
                });
        // If all config registration is successful, then we can change status to ready.
        this.ready();
    }

    public void ready() {
        /**
         * TODO: Emit a ready event to the watcher of this subsytem. Abstrasct all out
         * to common interface and write unit tests offline.
         */
        log.debug("config manager is ready.");
    }

    /**
     * A utility function to get a rule from a string.
     * 
     * @param ruleName The name of the rule to get.
     * @throws RuntimeException If the rule is not found.
     * 
     * @return The Rule object with the specified name.
     */
    public Rule getRuleFromStr(String ruleName) {
        return rules.keySet().stream()
                .filter(r -> r.getClass().getSimpleName().equalsIgnoreCase(ruleName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Rule not found"));
    }

    /**
     * A utility function to enable a rule by name.
     * 
     * @param ruleName The name of the rule to enable.
     * @throws RuntimeException If the rule is not found.
     * 
     * @return True if the rule was enabled, false otherwise.
     */
    public boolean enableRule(String ruleName) {
        Rule rule = getRuleFromStr(ruleName);
        return enableRule(rule);
    }

    /**
     * A utility function to enable a rule.
     * 
     * @param rule The rule to enable.
     * @throws RuntimeException If the rule is already enabled.
     * 
     * @return True if the rule was enabled, false otherwise.
     */
    public boolean enableRule(Rule rule) {
        // Throw an exception if the rule is already enabled.
        if (rules.get(rule))
            throw new RuntimeException("Rule already enabled");

        if (rule instanceof Listener listener)
            Bukkit.getPluginManager().registerEvents(listener, MonadUHC.instance());

        return rules.put(rule, true);
    }

    /**
     * A utility function to disable a rule by name.
     * 
     * @param ruleName The name of the rule to disable.
     * @throws RuntimeException If the rule is not found.
     * 
     * @return True if the rule was disabled, false otherwise.
     */
    public boolean disableRule(String ruleName) {
        Rule rule = getRuleFromStr(ruleName);
        return disableRule(rule);
    }

    /**
     * A utility function to disable a rule.
     * 
     * @param rule The rule to disable.
     * @throws RuntimeException If the rule is not enabled.
     * 
     * @return True if the rule was disabled, false otherwise.
     */
    public boolean disableRule(Rule rule) {
        // Throw an exception if the rule is not enabled.
        var ruleStatus = rules.get(rule);

        if (!ruleStatus) {
            log.info("Rule not enabled. " + rule.name() + ", " + ruleStatus);
            throw new RuntimeException("Rule not enabled");
        }

        if (rule instanceof Listener listener)
            HandlerList.unregisterAll(listener);

        log.info("Rule disabled. " + rule.name() + ", " + ruleStatus);

        return rules.put(rule, false);
    }

    /**
     * A utility function to toggle a rule by name.
     * 
     * @param ruleName The name of the rule to toggle.
     * @throws RuntimeException If the rule is not found.
     * 
     * @return True if the rule was toggled, false otherwise.
     */
    public boolean toggleRule(String ruleName) {
        Rule rule = getRuleFromStr(ruleName);
        return toggleRule(rule);
    }

    /**
     * A utility function to toggle a rule.
     * 
     * @param rule The rule to toggle.
     * @throws RuntimeException If the rule is not found.
     * 
     * @return True if the rule was toggled, false otherwise.
     */
    public boolean toggleRule(Rule rule) {
        // Throw an exception if the rule is not enabled.
        var ruleStatus = rules.get(rule);

        return ruleStatus ? disableRule(rule) : enableRule(rule);
    }

    /**
     * @return The rules hashMap, where the key is the Rule object, and the value is
     *         wether it is enabled or not.
     */
    public Map<Rule, Boolean> rulesMap() {
        return rules;
    }

    /**
     * @return An immutable list of all the registered rules.
     */
    public List<Rule> rules() {
        return rules.keySet().stream().collect(Collectors.toList());
    }
}
