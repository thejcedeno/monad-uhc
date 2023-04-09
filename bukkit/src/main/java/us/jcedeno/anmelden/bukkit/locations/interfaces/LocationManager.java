package us.jcedeno.anmelden.bukkit.locations.interfaces;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * An interface for all the methods the 
 */
public interface LocationManager {

    public World getLobby();

    public World getGameWorld();

    public Location getLobbySpawnPoint();

    public Location getScatterLocation(World world, int x, int z, int radius);

    public Set<Location> getScatterLocations(World world, int x, int z, int radius, int minDistance, int amount);

    public CompletableFuture<Runnable> executeScatterOperation();

}
