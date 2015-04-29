package net.coderodde.puzzle.graph;

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
     * Compares this node to <code>o</code>. If they deemed to encode the same
     * node, should return <code>true</code>.
     * 
     * @param  o the object to compare against.
     * @return <code>true</code> only if the two objects encode the same node.
     */
    public abstract boolean equals(final Object o);
}
