package us.jcedeno.anmelden.bukkit.scenarios.models;

public interface BaseScenario {

    public String name();
    public String description();

    abstract void init();

}
