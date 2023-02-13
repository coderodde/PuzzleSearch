package net.coderodde.puzzle.graph.finders.support;

import java.util.ArrayList;
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
 * This class implements a NBA*.
 * 
 * @author Rodion Efremov
 * @version 1.6
 * @param <T> the node type.
 */
public class NBAFinder<T extends AbstractGraphNode<T>>
extends AbstractPathFinder<T> {

    private final AbstractHeuristicFunction<T> heuristicFunction;
    private final AbstractHeuristicFunction<T> heuristicFunctionRev;
    private final IntegerPriorityQueue<?, T> queue;
    private int fA;
    private int fB;
    private int bestPathLength = Integer.MAX_VALUE;
    private T touchNode;
    
    public NBAFinder(final AbstractHeuristicFunction<T> heuristicFunction,
                     final IntegerPriorityQueue<?, T> queue) {
        this.heuristicFunction = heuristicFunction;
        this.heuristicFunctionRev = heuristicFunction.spawn();
        this.queue = queue;
    }
    
    public NBAFinder(final AbstractHeuristicFunction<T> heuristicFunction) {
        this(heuristicFunction, new DaryHeap<T>());
    }
     
    @Override
    public List<T> search(final T source, final T target) {
        if (source.equals(target)) {
            final List<T> path = new ArrayList<>(1);
            path.add(source);
            return path;
        }
        
        //// State
        final IntegerPriorityQueue<?, T> OPENA = queue.spawn();
        final IntegerPriorityQueue<?, T> OPENB = queue.spawn();
        
        final Set<T> CLOSED = new HashSet<>();
        
        final Map<T, T> PARENTSA = new HashMap<>();
        final Map<T, T> PARENTSB = new HashMap<>();
        
        final Map<T, Integer> DISTANCEA = new HashMap<>();
        final Map<T, Integer> DISTANCEB = new HashMap<>();
        
        heuristicFunction.setTarget(target);
        heuristicFunctionRev.setTarget(source);
        
        int totalDistance = heuristicFunction.estimate(target);
        this.fA = totalDistance;
        this.fB = totalDistance;
        this.bestPathLength = Integer.MAX_VALUE;
        
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
            
            if (touchNode != null) {
                final int fScoreA = DISTANCEA.get(minA)
                                  + heuristicFunction.estimate(minA);
                
                final int fScoreB = DISTANCEB.get(minB)
                                  + heuristicFunctionRev.estimate(minB);
                
                if (Math.max(fScoreA, fScoreB) >= bestPathLength) {
                    return tracebackPath(
                            touchNode, 
                            PARENTSA, 
                            PARENTSB);
                }
            }
            
            if (OPENA.size() < OPENB.size()) {
                expandInForwardDirection(
                        OPENA,
                        CLOSED,
                        DISTANCEA,
                        DISTANCEB, 
                        PARENTSA,
                        heuristicFunction, 
                        heuristicFunctionRev);
            } else {
                expandInBackwardDirection(
                        OPENB,
                        CLOSED, 
                        DISTANCEA, 
                        DISTANCEB, 
                        PARENTSB, 
                        heuristicFunction, 
                        heuristicFunctionRev);
            }
        }
        
        return null;
    }
    
    private void expandInForwardDirection(
            final IntegerPriorityQueue<?, T> OPENA,
            final Set<T> CLOSED,
            final Map<T, Integer> DISTANCEA,
            final Map<T, Integer> DISTANCEB,
            final Map<T, T> PARENTSA,
            final AbstractHeuristicFunction<T> heuristicFunction,
            final AbstractHeuristicFunction<T> heuristicFunctionRev) {
        T currentNode = OPENA.extractMinimum();
        
        if (CLOSED.contains(currentNode)) {
            return;
        }
        
        CLOSED.add(currentNode);
        
        if (DISTANCEA.get(currentNode)
                + heuristicFunction.estimate(currentNode)
                >= bestPathLength
                ||
                DISTANCEA.get(currentNode)
                + fB 
                - heuristicFunctionRev.estimate(currentNode) 
                >= bestPathLength) {
            // Reject the 'currentNode'.
        } else {
            // Stabilize the 'currentNode'.
            for (T childNode : currentNode) {
                if (CLOSED.contains(childNode)) {
                    continue;
                }
                
                int tentativeDistance = DISTANCEA.get(currentNode) + 1;
                
                if (!DISTANCEA.containsKey(childNode) 
                        || DISTANCEA.get(childNode) 
                        > tentativeDistance) {
                    DISTANCEA.put(childNode, tentativeDistance);
                    PARENTSA.put(childNode, currentNode);
                    
                    OPENA.insert(
                            childNode,
                            tentativeDistance 
                                    + heuristicFunction.estimate(
                                            childNode));
                    
                    if (DISTANCEB.containsKey(childNode)) {
                        final int pathLength = tentativeDistance 
                                             + DISTANCEB.get(childNode);

                        if (bestPathLength > pathLength) {
                            bestPathLength = pathLength;
                            touchNode = childNode;
                        }
                    }
                }
            }
        }
        
        if (!OPENA.isEmpty()) {
            fA = OPENA.minPriority();
        }
    }
    
    private void expandInBackwardDirection(
            final IntegerPriorityQueue<?, T> OPENB,
            final Set<T> CLOSED,
            final Map<T, Integer> DISTANCEA,
            final Map<T, Integer> DISTANCEB,
            final Map<T, T> PARENTSB,
            final AbstractHeuristicFunction<T> heuristicFunction,
            final AbstractHeuristicFunction<T> heuristicFunctionRev) {
        T currentNode = OPENB.extractMinimum();
        
        if (CLOSED.contains(currentNode)) {
            return;
        }
        
        CLOSED.add(currentNode);
        
        if (DISTANCEB.get(currentNode)
                + heuristicFunctionRev.estimate(currentNode)
                >= bestPathLength
                ||
                DISTANCEB.get(currentNode)
                + fA 
                - heuristicFunction.estimate(currentNode) 
                >= bestPathLength) {
            // Reject the 'currentNode'.
        } else {
            // Stabilize the 'currentNode'.
            for (T parentNode : currentNode.parents()) {
                if (CLOSED.contains(parentNode)) {
                    continue;
                }
                
                int tentativeDistance = DISTANCEB.get(currentNode) + 1;
                
                if (!DISTANCEB.containsKey(parentNode) 
                        || DISTANCEB.get(parentNode) 
                        > tentativeDistance) {
                    DISTANCEB.put(parentNode, tentativeDistance);
                    PARENTSB.put(parentNode, currentNode);
                    
                    OPENB.insert(
                            parentNode,
                            tentativeDistance 
                                    + heuristicFunctionRev.estimate(
                                            parentNode));
                    
                    if (DISTANCEA.containsKey(parentNode)) {
                        final int pathLength = tentativeDistance 
                                             + DISTANCEA.get(parentNode);

                        if (bestPathLength > pathLength) {
                            bestPathLength = pathLength;
                            touchNode = parentNode;
                        }
                    }
                }
            }
        }
        
        if (!OPENB.isEmpty()) {
            fB = OPENB.minPriority();
        }
    }
}
