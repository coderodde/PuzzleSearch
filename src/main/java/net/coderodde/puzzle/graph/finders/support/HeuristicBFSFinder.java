package net.coderodde.puzzle.graph.finders.support;

import java.util.List;
import net.coderodde.puzzle.graph.AbstractGraphNode;
import net.coderodde.puzzle.graph.AbstractHeuristicFunction;
import net.coderodde.puzzle.graph.finders.AbstractPathFinder;

/**
 * This class implements heuristic breadth-first search.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class HeuristicBFSFinder<T extends AbstractGraphNode<T>> 
extends AbstractPathFinder<T> {

    private final AbstractHeuristicFunction<T, Integer> heuristicFunction;
    
    public HeuristicBFSFinder(final AbstractHeuristicFunction<T> heuristicFunction) {
        
    }
    
    @Override
    public List<T> search(final T source, final T target) {
        
    }
}
