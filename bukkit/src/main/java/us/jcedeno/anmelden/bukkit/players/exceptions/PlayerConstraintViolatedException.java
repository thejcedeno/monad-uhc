package us.jcedeno.anmelden.bukkit.players.exceptions;

/**
 * An exception to be thrown when a player data or state constraint is violated.
 * 
 * @author thejcedeno
 */
public class PlayerConstraintViolatedException extends RuntimeException {
    
    /**
     * A constructor for the exception.
     * 
     * @param message   the message to be displayed.
     * @param throwable the throwable to be thrown.
     */
    public PlayerConstraintViolatedException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
