package us.jcedeno.anmelden.bukkit.players.models;

import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import us.jcedeno.anmelden.bukkit._utils.GlobalUtils.SerializedObject;

/**
 * A data class that represents a serializable UHC Player object.
 * 
 * @author thejcedeno
 */
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor(staticName = "of")
public class GamePlayer {
    protected final UUID uuid;
    private final String lastKnownName;
    private final GamePlayerData gamePlayerData;
    private final UHCPlayerData uhcPlayerData;
    // This to track afk/abandonment.
    private Long lastConnnectionTime;
    private Long lastDisconnectionTime;
    /**
     * A dataclass to hold all the game player state that we need to save.
     */
    @AllArgsConstructor(staticName = "of")
    public static class GamePlayerData {
        private final Vector location;
        private final Double health;
        private final PlayerInventory inventory;
        private final List<SerializedObject<PotionEffect>> potionEffects;
        private final int foodLevel;
        private final float saturation;
        private final float experience;
    }

    /**
     * A data class to represent all the statistis that we collect per player.
     * 
     * @author thejcedeno
     */
    @Data
    @AllArgsConstructor(staticName = "of")
    public static class UHCPlayerData {
        private int kills;
        private int ironMined;
        private int goldMined;
        private int diamondMined;
        private int emeraldMined;
        private int lapisMined;
        private int redstoneMined;
        private int coalMined;
        private int applesEaten;
        private int gapplesEaten;
        private int goldenHeadsEaten;
        private int damageDealt;
        private int damageTaken;
        private int blocksBroken;
        private int blocksPlaced;
        private int arrowsShot;
        private int arrowsHit;
        private Long firstDamageTaken;

    }
}
