package us.jcedeno.anmelden.bukkit.scenarios.models;

/**
 * An interface that all the scenarios must implement.
 * 
 * This interface is used to make sure all the scenarios have the same methods
 * and properties.
 * 
 * @author thejcedeno
 */
public interface IScenario {

    public String name();

    public String description();

    /**
     * A method intended to be called when the scenario is first initialized.
     * 
     * When extending other plugins, overriding this method is recommended to set
     * custom properties.
     */
    abstract void init();

}
