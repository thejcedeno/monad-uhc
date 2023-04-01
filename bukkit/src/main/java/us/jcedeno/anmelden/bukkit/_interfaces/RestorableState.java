package us.jcedeno.anmelden.bukkit._interfaces;

/**
 * This interface is used to allow objects to be snapshotted and restored in case
 * of a sudden shutdown or backup attempt.
 * 
 * @param <T> The object to snapshot
 */
public interface RestorableState<T> {
    /**
     * Takes a snapshot of the current state of the object, and the implementation
     * is expected to serialized it to JSON and return it as a string. This method
     * will be caled in case of a sudden shutdown request or backup attempt.
     * 
     * @param t The object to snapshot
     * 
     * @return The JSON string of the snapshot.
     */
    public String snapshot(T t);

    /**
     * Restores the object to the state it was in when the snapshot was taken. This
     * method will be called in case of a sudden shutdown request or backup attempt.
     * 
     * @param t The object to restore
     * @param json The JSON string of the snapshot
     */
    public void restore(T t, String json);
}
