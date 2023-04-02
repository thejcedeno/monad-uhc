package us.jcedeno.anmelden.bukkit.scenarios.models;

public interface IScenario {

    public String name();
    public String description();

    abstract void init();

}
