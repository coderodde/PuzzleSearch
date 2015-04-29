package net.coderodde.puzzle.util;

/**
 * This interface defines the API for a spawnable object. Every type
 * implementing this interface can be duplicated (spawned) by calling the 
 * {@code spawn} method.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public interface Spawnable<T> {
    
    /**
     * Spawns a new object of type <code>T</code>.
     * 
     * @return a new object with some default state.
     */
    public T spawn();
}
