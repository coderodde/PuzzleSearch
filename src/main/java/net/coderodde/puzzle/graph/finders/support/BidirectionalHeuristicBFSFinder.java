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
 * This class implements a bidirectional heuristic breadth-first search.
 * 
 * @author Rodion Efremov
 * @version 1.6
 * @param <T> the node type.
 */
public class BidirectionalHeuristicBFSFinder<T extends AbstractGraphNode<T>>
extends AbstractPathFinder<T> {

    private final AbstractHeuristicFunction<T> heuristicFunction;
    private final AbstractHeuristicFunction<T> heuristicFunctionRev;
    private final IntegerPriorityQueue<?, T> queue;
    
    public BidirectionalHeuristicBFSFinder(
            final AbstractHeuristicFunction<T> heuristicFunction,
            final IntegerPriorityQueue<?, T> queue) {
        this.heuristicFunction = heuristicFunction;
        this.heuristicFunctionRev = heuristicFunction.spawn();
        this.queue = queue;
    }
    
    public BidirectionalHeuristicBFSFinder(
        final AbstractHeuristicFunction<T> heuristicFunction) {
        this(heuristicFunction, new DaryHeap<T>());
    }
    @Override
    public List<T> search(final T source, final T target) {
        //// State
        final IntegerPriorityQueue<?, T> OPENA = queue.spawn();
        final IntegerPriorityQueue<?, T> OPENB = queue.spawn();
        
        final Set<T> CLOSEDA = new HashSet<>();
        final Set<T> CLOSEDB = new HashSet<>();
        
        final Map<T, T> PARENTSA = new HashMap<>();
        final Map<T, T> PARENTSB = new HashMap<>();
        
        final Map<T, Integer> DISTANCEA = new HashMap<>();
        final Map<T, Integer> DISTANCEB = new HashMap<>();
        
        int bestCost = Integer.MAX_VALUE;
        T touchNode = null;
        
        // Initialization
        heuristicFunction.setTarget(target);
        heuristicFunctionRev.setTarget(source);
        
        OPENA.insert(source, 0);
        OPENB.insert(target, 0);
        
        PARENTSA.put(source, null);
        PARENTSB.put(target, null);
        
        DISTANCEA.put(source, 0);
        DISTANCEB.put(target, 0);
        
        while (!OPENA.isEmpty() && !OPENB.isEmpty()) {
            final T minA = OPENA.min();
            final T minB = OPENB.min();
            
            final int distA = DISTANCEA.get(minA);
            final int distB = DISTANCEB.get(minB);
            
            if (touchNode != null) {
                final int fA = distA + heuristicFunction.estimate(minA);
                final int fB = distB + heuristicFunctionRev.estimate(minB);
                
                if (Math.max(fA, fB) >= bestCost) {
                    return tracebackPath(touchNode, PARENTSA, PARENTSB);
                }
            }
            
            if (distA < distB) {
                // Expand the forward search frontier.
                final T current = OPENA.extractMinimum();
                
                CLOSEDA.add(current);
                
                for (final T child : current) {
                    if (CLOSEDA.contains(child)) {
                        continue;
                    }
                    
                    final int g = DISTANCEA.get(current) + 1;
                    
                    if (!PARENTSA.containsKey(child)) {
                        PARENTSA.put(child, current);
                        DISTANCEA.put(child, g);
                        OPENA.insert(child, 
                                     g + heuristicFunction.estimate(child));
                        
                        if (CLOSEDB.contains(child)) {
                            final int cost = g + DISTANCEB.get(child);
                            
                            if (bestCost > cost) {
                                bestCost = cost;
                                touchNode = child;
                            }
                        }
                    } else if (DISTANCEA.get(child) > g) {
                        DISTANCEA.put(child, g);
                        PARENTSA.put(child, current);
                        OPENA.decreasePriority(child,
                                               g + heuristicFunction
                                                   .estimate(child));
                        
                        if (CLOSEDB.contains(child)) {
                            final int cost = g + DISTANCEB.get(child);
                            
                            if (bestCost > cost) {
                                bestCost = cost;
                                touchNode = child;
                            }
                        }
                    }
                }
            } else {
                // Expand the backward search frontier.
                final T current = OPENB.extractMinimum();
                
                CLOSEDB.add(current);
                
                for (final T parent : current.parents()) {
                    if (CLOSEDB.contains(parent)) {
                        continue;
                    }
                    
                    final int g = DISTANCEB.get(current) + 1;
                    
                    if (!PARENTSB.containsKey(parent)) {
                        PARENTSB.put(parent, current);
                        DISTANCEB.put(parent, g);
                        OPENB.insert(parent, g + heuristicFunctionRev
                                                 .estimate(parent));
                        
                        if (CLOSEDA.contains(parent)) {
                            final int cost = g + DISTANCEA.get(parent);
                            
                            if (bestCost > cost) {
                                bestCost = cost;
                                touchNode = parent;
                            }
                        }
                    } else if (DISTANCEB.get(parent) > g) {
                        DISTANCEB.put(parent, g);
                        PARENTSB.put(parent, current);
                        OPENB.decreasePriority(parent, g + heuristicFunctionRev
                                                           .estimate(parent));
                        if (CLOSEDA.contains(parent)) {
                            final int cost = g + DISTANCEA.get(parent);
                            
                            if (bestCost > cost) {
                                bestCost = cost;
                                touchNode = parent;
                            }
                        }
                    }
                }
            }
        }
        
        // Target unreachable from source.
        return null;
    }
}
