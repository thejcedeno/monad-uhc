package us.jcedeno.anmelden.bukkit.config.models;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.jcedeno.anmelden.bukkit.scenarios.models.IScenario;

/**
 * A dataclass to represent a UHC Game Configuration. This is used to store and
 * restore game configurations. Serializable to JSON.
 * 
 * TODO: Consider saving InitialGameConfig at second 0, then blocking certain
 * changes to certain config as time goes along and only serialize the object to
 * save it for failures when a change is made to any of the values in this
 * object. Use timestamp in restorable json object for stored state.
 * 
 * @author thejcedeno
 */
@Getter
@Setter
@NoArgsConstructor(staticName = "create")
@AllArgsConstructor
public class GameConfig {
    private UUID _hostId;
    private UUID _internalTrackingId;
    private String matchName;

    // TODO: Add this functionality to the team manager instead.
    private String teamConfig = "cTo2"; // cTo, rTo3, cTo4, ffa,
    // Global Listener that limits players without bypass permission to join.   
    private int maxPlayers = 70;

    // Game Timed Event cfg values, in secs.
    private Long finalHeal = 2L * 60_000;
    private Long pvp = 15L * 60_000;
    private Long meetup = 60L * 60_000;


}
