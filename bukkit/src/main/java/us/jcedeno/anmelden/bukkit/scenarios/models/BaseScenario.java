package us.jcedeno.anmelden.bukkit.scenarios.models;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BaseScenario implements IScenario{
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
    
}
