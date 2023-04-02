package us.jcedeno.anmelden.bukkit.config.models;

import java.util.List;
import java.util.UUID;

import lombok.Data;
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
@Data
public class GameConfig {
    private UUID hostId;
    private UUID internalTrackingId;
    private String matchName;

    // TODO: Use dedidcated object for this later
    private List<String> whitelist; // List of UUIDs or offlineplayer names.

    // TODO: replace with better object
    private String teamConfig = "cTo2"; // cTo, rTo3, cTo4, ffa, 

    // Game Settings
    private List<IScenario> scenarios;
    private boolean nether;
    private boolean end;
    private boolean pvp;

    private int maxPlayers;

    // Game Timings stuff.
    private Long pvptime = 15L * 60 * 1000;
    private Long meetupTime = 60L * 60 * 1000;

    // Timings stuff
    private long expectedEndTime;
    private long expectedStartTime;

}
