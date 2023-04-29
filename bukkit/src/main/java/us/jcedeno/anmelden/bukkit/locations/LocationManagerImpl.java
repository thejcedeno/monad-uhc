package us.jcedeno.anmelden.bukkit.locations;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import us.jcedeno.anmelden.bukkit.locations.interfaces.LocationManager;

/**
 * Handles the world generation, terrain generation, and handles the player
 * scattering logic.
 * 
 * @author thejcedeno
 */
public class LocationManagerImpl implements LocationManager {

    @Override
    public World getLobby() {
        return Bukkit.getWorld("lobby");
    }

    @Override
    public World getGameWorld() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getGameWorld'");
    }

    @Override
    public Location getLobbySpawnPoint() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLobbySpawnPoint'");
    }

    @Override
    public Location getScatterLocation(World world, int x, int z, int radius) {

        // Find random block in radius centered at x,z
        int rx = x + (int) (Math.random() * radius * 2) - radius;
        int rz = z + (int) (Math.random() * radius * 2) - radius;

        // Find highest block at random location
        var b = world.getHighestBlockAt(rx, rz);

        return b.getRelative(BlockFace.UP).getLocation().add(0.5, 60, 0.5);

    }

    @Override
    public Set<Location> getScatterLocations(World world, int x, int z, int radius, int minDistance, int amount) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getScatterLocations'");
    }

    @Override
    public CompletableFuture<Runnable> executeScatterOperation() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'executeScatterOperation'");
    }

}
