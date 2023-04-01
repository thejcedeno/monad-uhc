package us.jcedeno.anmelden.bukkit._models;

/**
 * An abstract class that represent the context of a game. This is the definition of the game loop and how to serialize 
 * 
 * @author thejcedeno
 */
public abstract class Context {
    //  The game loop of the context definition
    public abstract void run();
    // A JSON representation of the context.
    public abstract String serialized();
    
}
