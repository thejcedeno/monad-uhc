package us.jcedeno.anmelden.bukkit.config.impl;

import us.jcedeno.anmelden.bukkit.config.ConfigManager;
import us.jcedeno.anmelden.bukkit.config.annotations.Setting;
import us.jcedeno.anmelden.bukkit.config.models.Rule;

/**
 * An implementation of the Rule interface to represent in game toggeable
 * settings.
 * 
 * @author thejcedeno
 */
@Setting(name = "global-rule", description = "Controls nothing.")
public class GSetting implements Rule {
    protected String name;
    protected String description;

    /**
     * Creates a new Game Setting with the given name and description.
     * 
     * @param name        the name of the setting.
     * @param description the description of the setting.
     */
    public GSetting(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public boolean toggle(ConfigManager ruleManager) {
        throw new UnsupportedOperationException("Unimplemented method 'toggle'");
    }

}
