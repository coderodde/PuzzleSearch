package net.coderodde.puzzle.graph.finders.support;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import net.coderodde.puzzle.graph.AbstractGraphNode;
import net.coderodde.puzzle.graph.finders.AbstractPathFinder;

/**
 *
 * @author Rodion Efremov
 * @version 1.6
 */
public class BidirectionalBFSFinder<T extends AbstractGraphNode<T>>
extends AbstractPathFinder<T> {

    @Override
    public List<T> search(final T source, final T target) {
        final Queue<T> queueA = new ArrayDeque<>();
        final Queue<T> queueB = new ArrayDeque<>();
        
        final Map<T, T> parentMapA = new HashMap<>();
        final Map<T, T> parentMapB = new HashMap<>();
        
        final Map<T, Integer> distanceMapA = new HashMap<>();
        final Map<T, Integer> distanceMapB = new HashMap<>();
        
        queueA.add(source);
        queueB.add(target);
        
        parentMapA.put(source, null);
        parentMapB.put(target, null);
        
        distanceMapA.put(source, 0);
        distanceMapB.put(target, 0);
        
        int bestCost = Integer.MAX_VALUE;
        T touchNode = null;
        
        while (!queueA.isEmpty() && !queueB.isEmpty()) {
            final int distanceA = distanceMapA.get(queueA.peek());
            final int distanceB = distanceMapB.get(queueB.peek());
            
            if (touchNode != null && bestCost < distanceA + distanceB) {
                return tracebackPath(touchNode, parentMapA, parentMapB);
            }
            
            if (distanceA < distanceB) {
                // Trivial load balancing.
                final T current = queueA.poll();
                
                if (distanceMapB.containsKey(current) 
                        && bestCost > distanceMapA.get(current) +
                                      distanceMapB.get(current)) {
                    bestCost = distanceMapA.get(current) + 
                               distanceMapB.get(current);
                    touchNode = current;
                }
                
                for (final T child : current) {
                    if (!distanceMapA.containsKey(child)) {
                        distanceMapA.put(child, distanceMapA.get(current) + 1);
                        parentMapA.put(child, current);
                        queueA.add(child);
                    }
                }
            } else {
                final T current = queueB.poll();
                
                if (distanceMapA.containsKey(current) 
                        && bestCost > distanceMapA.get(current) +
                                      distanceMapB.get(current)) {
                    bestCost = distanceMapA.get(current) + 
                               distanceMapB.get(current);
                    touchNode = current;
                }
                
                for (final T parent : current.parents()) {
                    if (!distanceMapB.containsKey(parent)) {
                        distanceMapB.put(parent, distanceMapB.get(current) + 1);
                        parentMapB.put(parent, current);
                        queueB.add(parent);
                    }
                }
            }
        }
        
        // Target not reachable from source.
        return null;
    }
}
