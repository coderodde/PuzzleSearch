package net.coderodde.puzzle.graph;

import net.coderodde.puzzle.util.Spawnable;

/**
 * This abstract class specifies the API for a heuristic function.
 * 
 * @author Rodion Efremov
 * @version 1.6
 * @param <T> the actual node type.
 */
public abstract class AbstractHeuristicFunction<T> 
implements Spawnable<AbstractHeuristicFunction<T>>{
    
    /**
     * Returns the estimate for the length of the path from <code>source</code> 
     * to the preset target node.
     * 
     * @param  source the source node.
     * @return the estimate.
     */
    public abstract int estimate(final T source);
    
    /**
     * Sets the target node.
     * 
     * @param target the target node.
     */
    public abstract void setTarget(final T target);
}
