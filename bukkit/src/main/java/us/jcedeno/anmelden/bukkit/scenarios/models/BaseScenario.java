package us.jcedeno.anmelden.bukkit.scenarios.models;

import lombok.AllArgsConstructor;

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

}
