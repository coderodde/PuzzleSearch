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
    private int[] xArray = new int[0];
    
    /**
     * Implements a simple map from a cell number to its y-coordinate.
     */
    private int[] yArray = new int[0];
    
    /**
     * Caches the target node.
     */
    private PuzzleGraphNode target;
    
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
        
        ensureCapacity(degree * degree);
        
        int distance = 0;
        
        for (int y = 0; y < degree; ++y) {
            for (int x = 0; x < degree; ++x) {
                final byte currentCell = source.get(x, y);
                xArray[currentCell] = x;
                yArray[currentCell] = y;
            }
        }
        
        for (int y = 0; y < degree; ++y) {
            for (int x = 0; x < degree; ++x) {
                final byte currentCell = target.get(x, y);
                
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
    
    /**
     * Ensures that the coordinate arrays can accommodate <code>capacity</code>
     * amount of entries.
     * 
     * @param capacity the requested capacity.
     */
    private void ensureCapacity(final int capacity) {
        if (xArray.length < capacity) {
            xArray = new int[capacity];
            yArray = new int[capacity];
        }
    }

    @Override
    public AbstractHeuristicFunction<PuzzleGraphNode> spawn() {
        return new ManhattanHeuristicFunction();
    }
}
