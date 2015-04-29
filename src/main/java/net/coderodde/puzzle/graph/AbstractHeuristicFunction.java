package net.coderodde.puzzle.graph;

/**
 * This abstract class specifies the API for a heuristic function.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public abstract class AbstractHeuristicFunction<T> {
    
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
