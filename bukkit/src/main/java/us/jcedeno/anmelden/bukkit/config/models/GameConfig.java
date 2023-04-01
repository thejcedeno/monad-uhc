package us.jcedeno.anmelden.bukkit.config.models;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A dataclass to represent a UHC Game Configuration. This is used to store and
 * restore game configurations. Serializable to JSON.
 * 
 * @author thejcedeno
 */
@Data
public class GameConfig {
    private UUID hostId;
    private String matchName;

    private int maxPlayers;

}
