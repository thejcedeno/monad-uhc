package us.jcedeno.anmelden.bukkit.locations;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import org.bukkit.Location;
import org.bukkit.World;

import us.jcedeno.anmelden.bukkit.locations.interfaces.LocationManager;

/**
 * Handles the world generation, terrain generation, and handles the player scattering logic.
 * 
 * @author thejcedeno
 */
public class LocationManagerImpl implements LocationManager{

    @Override
    public World getLobby() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLobby'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getScatterLocation'");
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
