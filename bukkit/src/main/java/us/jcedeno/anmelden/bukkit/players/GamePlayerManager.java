package us.jcedeno.anmelden.bukkit.players;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import us.jcedeno.anmelden.bukkit.players.exceptions.PlayerConstraintViolatedException;
import us.jcedeno.anmelden.bukkit.players.models.GamePlayer;

/**
 * A class to manage all the {@link GamePlayer} objects in a game instance.
 * 
 * @author thejcedeno
 */
@RequiredArgsConstructor
public class GamePlayerManager {
    @NonNull
    private volatile Map<UUID, GamePlayer> gamePlayers = new ConcurrentHashMap<>();

    /**
     * @return a {@link GamePlayer} object from the given {@link UUID}.
     */
    public Optional<GamePlayer> gamePlayer(UUID uuid) {
        return Optional.ofNullable(gamePlayers.get(uuid));
    }

    /**
     * @param OfflinePlayer the player to be registers. NOTE: this method results in
     *                      NULL gamePlayer and uhcPlayer data. MIGHT need to
     *                      reinitialize variables later.
     * 
     * @throws PlayerConstraintViolatedException if the player is already
     *                                           registered.
     * 
     * @return a {@link GamePlayer} object from the given {@link Player}.
     */
    public Optional<GamePlayer> registerGamePlayer(OfflinePlayer offlinePlayer) {
        var uuid = offlinePlayer.getUniqueId();

        if (gamePlayers.containsKey(uuid))
            throw new PlayerConstraintViolatedException("You tried to register a player that's already registered.",
                    new Exception(uuid + " already registered"));

        var gamePlayer = GamePlayer.of(uuid, offlinePlayer.getName(), null, null, offlinePlayer.getLastLogin(),
                offlinePlayer.getLastSeen());

        return Optional.ofNullable(gamePlayers.put(uuid, gamePlayer));
    }

}
