package net.coderodde.puzzle.graph;

import java.util.List;

/**
 * This abstract class defines the API for a graph node.
 * 
 * @author Rodion Efremov
 * @version 1.6
 * @param <T> the actual implementation type.
 */
public abstract class AbstractGraphNode<T extends AbstractGraphNode<T>> 
implements Iterable<T> {
    
    /**
     * Returns an {@code Iterable} over this node's parent nodes.
     * 
     * @return parent nodes.
     */
    public abstract Iterable<T> parents();
    
    /**
     * Returns the hash code of this node.
     * 
     * @return the hash code.
     */
    public abstract int hashCode();
    
    /**
     * Checks whether there is an edge from this node to <code>other</code>.
     * 
     * @param  other the child node candidate.
     * @return <code>true</code> if and only if this node is connected to 
     *         <code>other</code>.
     */
    public abstract boolean hasChild(final T other);
    
    /**
     * Compares this node to <code>o</code>. If they deemed to encode the same
     * node, should return <code>true</code>.
     * 
     * @param  o the object to compare against.
     * @return <code>true</code> only if the two objects encode the same node.
     */
    public abstract boolean equals(final Object o);
    
    public boolean isValidPath(final T source, 
                               final T target,
                               final List<T> path) {
        if (path.isEmpty()) {
            return false;
        }
        
        if (!source.equals(path.get(0))) {
            return false;
        }
        
        if (!target.equals(path.get(path.size() - 1))) {
            return false;
        }
        
        for (int i = 0; i < path.size() - 1; ++i) {
            if (!path.get(i).hasChild(path.get(i + 1))) {
                return false;
            }
        }
        
        return true;
    }
}
