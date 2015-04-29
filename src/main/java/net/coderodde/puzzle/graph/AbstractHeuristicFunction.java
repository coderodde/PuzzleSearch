package net.coderodde.puzzle.graph;

/**
 * This abstract class specifies the API for a heuristic function.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public abstract class AbstractHeuristicFunction<T, W> {
    
    /**
     * Get the estimate for the length of the path from <code>source</code> to 
     * <code>target</code>.
     * 
     * @param  source the source node.
     * @param  target the target node.
     * @return the estimate.
     */
    public abstract W estimate(final T source, final T target);
}
