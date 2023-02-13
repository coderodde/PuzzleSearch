package net.coderodde.puzzle.graph.finders.support;

import net.coderodde.puzzle.graph.AbstractHeuristicFunction;
import net.coderodde.puzzle.graph.support.PuzzleGraphNode;

/**
 * This class implements the Manhattan heuristic function.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class ManhattanHeuristicFunction 
extends AbstractHeuristicFunction<PuzzleGraphNode> {

    /**
     * Implements a simple map from a cell number to its x-coordinate.
     */
    private final int[] xArray;
    
    /**
     * Implements a simple map from a cell number to its y-coordinate.
     */
    private final int[] yArray;
    
    private final PuzzleGraphNode source;
    
    /**
     * Caches the target node.
     */
    private PuzzleGraphNode target;
    
    public ManhattanHeuristicFunction(
            PuzzleGraphNode sourceNode) {
        final int degree = sourceNode.getDegree();
        this.source = sourceNode;
        this.xArray = new int[degree * degree];
        this.yArray = new int[degree * degree];
    }
    
    /**
     * {@inheritDoc }
     * 
     * @param  source the source node whose estimate to compute.
     * @return the optimistic distance to <code>target</code> from 
     * <code>source</code>.
     */
    @Override
    public int estimate(final PuzzleGraphNode source) {
        final int degree = source.getDegree();
        int distance = 0;
        
        for (int y = 0; y < degree; ++y) {
            for (int x = 0; x < degree; ++x) {
                final int currentCell = source.get(x, y);
                
                if (currentCell < 0) {
                    break;
                }
                
                xArray[currentCell] = x;
                yArray[currentCell] = y;
            }
        }
        
        for (int y = 0; y < degree; ++y) {
            for (int x = 0; x < degree; ++x) {
                final int currentCell = target.get(x, y);
                
                distance += Math.abs(x - xArray[currentCell]) +
                            Math.abs(y - yArray[currentCell]);
            }
        }
        
        final int targetX = target.getEmptySlotX();
        final int targetY = target.getEmptySlotY();
        
        distance -= Math.abs(xArray[0] - targetX) +
                    Math.abs(yArray[0] - targetY);
        
        return distance;
    }

    /**
     * {@inheritDoc }
     * 
     * @param target the target node.
     */
    @Override
    public void setTarget(PuzzleGraphNode target) {
        this.target = target;
    }
    
    @Override
    public AbstractHeuristicFunction<PuzzleGraphNode> spawn() {
        return new ManhattanHeuristicFunction(source);
    }
}
