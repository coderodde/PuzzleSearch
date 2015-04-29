package net.coderodde.puzzle.graph.finders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.coderodde.puzzle.graph.AbstractGraphNode;

/**
 * This abstract class defines the common API for path finders and implements 
 * some functionality shared by the actual finders.
 * 
 * @author Rodion Efremov
 * @version 1.6
 * @param <T> the actual node type.
 */
public abstract class AbstractPathFinder<T extends AbstractGraphNode<T>> {
    
    /**
     * The entry point into a shortest path algorithm.
     * 
     * @param  source the source node.
     * @param  target the target node.
     * @return the list of nodes on a shortest path from <code>source</code>
     *         <code>target</code>, or <code>null</code> if <code>target</code>
     *         is unreachable from <code>source</code>.
     */
    public abstract List<T> search(final T source, final T target);
    
    /**
     * Constructs a path using a representative node one or two parent maps.
     * If <code>parentMap2</code> is <code>null</code>, <code>null</code> must
     * be the actual target node in order to build the valid path. Otherwise,
     * bidirectional search is assumed to have taken place, and in such 
     * condition <code>node</code> is assumed to be the "center" node where
     * the two search frontiers "meet".
     * 
     * @param node       the representative node.
     * @param parentMap1 the parent map in forward search.
     * @param parentMap2 the parent map in backward search. 
     *                   May be <code>null</code>, which denotes unidirectional
     *                   search.
     * @return a graph path.
     */
    protected List<T> tracebackPath(final T node, 
                                    final Map<T, T> parentMap1,
                                    final Map<T, T> parentMap2) {
        final List<T> path = new ArrayList<>();
        
        T current = node;
        
        while (current != null) {
            path.add(current);
            current = parentMap1.get(current);
        }
        
        Collections.<T>reverse(path);
        
        if (parentMap2 != null) {
            current = parentMap2.get(node);
            
            while (current != null) {
                path.add(current);
                current = parentMap2.get(current);
            }
        }
        
        return path;
    }
    
    /**
     * Constructs a path found by unidirectional search.
     * 
     * @param target    the target node.
     * @param parentMap the parent map.
     * @return a graph path.
     */
    protected List<T> tracebackPath(final T target, final Map<T, T> parentMap) {
        return tracebackPath(target, parentMap, null);
    }
}
