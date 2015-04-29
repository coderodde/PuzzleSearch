package net.coderodde.puzzle.graph.finders.support;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.coderodde.puzzle.graph.AbstractGraphNode;
import net.coderodde.puzzle.graph.AbstractHeuristicFunction;
import net.coderodde.puzzle.graph.finders.AbstractPathFinder;
import net.coderodde.puzzle.util.IntegerPriorityQueue;
import net.coderodde.puzzle.util.support.DaryHeap;

/**
 * This class implements heuristic breadth-first search.
 * 
 * @author Rodion Efremov
 * @version 1.6
 * @param <T> the actual graph node implementation type.
 */
public class HeuristicBFSFinder<T extends AbstractGraphNode<T>> 
extends AbstractPathFinder<T> {

    private final AbstractHeuristicFunction<T> heuristicFunction;
    private final IntegerPriorityQueue<?, T> queue;
    
    public HeuristicBFSFinder(
            final AbstractHeuristicFunction<T> heuristicFunction,
            final IntegerPriorityQueue<?, T> queue) {
        this.heuristicFunction = heuristicFunction;
        this.queue = queue;
    }
    
    public HeuristicBFSFinder(
            final AbstractHeuristicFunction<T> heuristicFunction) {
        this(heuristicFunction, new DaryHeap<T>());
    }
    
    @Override
    public List<T> search(final T source, final T target) {
        heuristicFunction.setTarget(target);
        
        final IntegerPriorityQueue<?, T> OPEN = queue.spawn();
        final Set<T> CLOSED = new HashSet<>();
        
        final Map<T, T> parentMap = new HashMap<>();
        final Map<T, Integer> distanceMap = new HashMap<>();
        
        OPEN.insert(source, 0);
        parentMap.put(source, null);
        distanceMap.put(source, 0);
        
        while (!OPEN.isEmpty()) {
            final T current = OPEN.extractMinimum();
            
            if (current.equals(target)) {
                return tracebackPath(target, parentMap);
            }
            
            CLOSED.add(current);
            
            for (final T child : current) {
                if (CLOSED.contains(child)) {
                    continue;
                }
                
                final int g = distanceMap.get(current) + 1;
                
                if (!parentMap.containsKey(child)) {
                    parentMap.put(child, current);
                    distanceMap.put(child, g);
                    OPEN.insert(child, g + heuristicFunction.estimate(child));
                } else if (distanceMap.get(child) > g) {
                    distanceMap.put(child, g);
                    parentMap.put(child, current);
                    OPEN.decreasePriority(child, 
                                          g + heuristicFunction.
                                              estimate(child));
                }
            }
        }
        
        return null;
    }
}
