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
extends AbstractHeuristicFunction<PuzzleGraphNode, Integer> {

    @Override
    public Integer estimate(final PuzzleGraphNode source, 
                            final PuzzleGraphNode target) {
        final int degree = source.getDegree();
        final int[] xs = new int[degree * degree];
        final int[] ys = new int[xs.length];
        int distance = 0;
        
        for (int y = 0; y < degree; ++y) {
            for (int x = 0; x < degree; ++x) {
                final byte currentCell = source.get(x, y);
                xs[currentCell] = x;
                ys[currentCell] = y;
            }
        }
        
        for (int y = 0; y < degree; ++y) {
            for (int x = 0; x < degree; ++x) {
                final byte currentCell = target.get(x, y);
                
                distance += Math.abs(x - xs[currentCell]) +
                            Math.abs(y - ys[currentCell]);
            }
        }
        
        return distance;
    }
}
