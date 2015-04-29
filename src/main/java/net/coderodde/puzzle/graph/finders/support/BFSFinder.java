package net.coderodde.puzzle.graph.finders.support;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import net.coderodde.puzzle.graph.AbstractGraphNode;
import net.coderodde.puzzle.graph.finders.AbstractPathFinder;

/**
 * This class implements breadth-first search.
 * 
 * @author Rodion Efremov
 * @version 1.6
 * @param <T> the actual node implementation.
 */
public class BFSFinder<T extends AbstractGraphNode<T>> 
extends AbstractPathFinder<T> {

    @Override
    public List<T> search(final T source, final T target) {
        final Queue<T> queue = new ArrayDeque<>();
        final Map<T, T> parentMap = new HashMap<>();
        
        queue.add(source);
        parentMap.put(source, null);
        
        while (!queue.isEmpty()) {
            final T current = queue.poll();
            
            if (current.equals(target)) {
                return tracebackPath(current, parentMap);
            }
            
            for (final T child : current) {
                if (!parentMap.containsKey(child)) {
                    parentMap.put(child, current);
                    queue.add(child);
                }
            }
        }
        
        // Target not reachable from source.
        return null;
    }
}
