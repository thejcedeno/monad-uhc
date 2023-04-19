package us.jcedeno.anmelden.bukkit.config.rules;

import java.util.Random;

import org.bukkit.event.Listener;

import us.jcedeno.anmelden.bukkit.config.ConfigManager;
import us.jcedeno.anmelden.bukkit.config.annotations.Setting;
import us.jcedeno.anmelden.bukkit.config.impl.GSetting;

/**
 * A class that contains all the rules for the potions scenario.
 * 
 * @author thejcedeno
 */
@Setting(name = "potions", description = "Controls the use of potions. Weather All potions are allowed, or only tier 1 potions.")
public class PotionsRules extends GSetting implements Listener {

    /**
     * Creates a new PotionsRules instance.
     * 
     * @param name        the name of the rule.
     * @param description the description of the rule.
     */
    public PotionsRules(String name, String description) {
        super(name, description);
    }


    @Override
    public boolean toggle(ConfigManager ruleManager) {
        System.out.println("Hi 🤓");
        return new Random().nextBoolean();
    }
}
