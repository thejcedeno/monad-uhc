package us.jcedeno.anmelden.bukkit.config.models;

import us.jcedeno.anmelden.bukkit.config.ConfigManager;

/*
 * An interface that all the rules must implement.
 * 
 * @author thejcedeno
 */
public interface Rule {
    /**
     * Returns the name of the rule.
     * 
     * @return the name of the rule.
     */
    public String name();

    /**
     * Returns the description of the rule.
     * 
     * @return the description of the rule.
     */
    public String description();

    public boolean toggle(ConfigManager ruleManager);
}
