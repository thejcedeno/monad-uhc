package us.jcedeno.anmelden.bukkit.scenarios.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * WIP: PROBABLY NOT NEEDED.
 */
@Data
@AllArgsConstructor(staticName = "of")
public class ScenarioUIElement {
    private String name;
    private String description;
    private boolean enabled;
    
}
