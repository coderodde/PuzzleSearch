package net.coderodde.puzzle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.coderodde.puzzle.graph.support.PuzzleGraphNode;
import net.coderodde.puzzle.graph.finders.AbstractPathFinder;
import net.coderodde.puzzle.graph.finders.support.BFSFinder;
import net.coderodde.puzzle.graph.finders.support.BidirectionalBFSFinder;
import net.coderodde.puzzle.graph.finders.support.HeuristicBFSFinder;
import net.coderodde.puzzle.graph.finders.support.ManhattanHeuristicFunction;

public class App {
    
    private static final int SCREEN_WIDTH = 80;
    
    public static void main(final String... args) {
        final long seed = System.currentTimeMillis();
        final Random rnd = new Random(seed);
        final PuzzleGraphNode source = getSource(15, rnd);
        final PuzzleGraphNode target = new PuzzleGraphNode(3);
        
        System.out.println("Seed: " + seed);
        
        final List<List<PuzzleGraphNode>> pathList = new ArrayList<>();
        
//        pathList.add(profile(new BFSFinder(), source, target));
        final ManhattanHeuristicFunction mhf = new ManhattanHeuristicFunction();
        final AbstractPathFinder<PuzzleGraphNode> hbfs = 
                new HeuristicBFSFinder(mhf);
        
        pathList.add(profile(new BidirectionalBFSFinder(), source, target));
        pathList.add(profile(hbfs, source, target));
        
        final List<PuzzleGraphNode>[] pathListArray = 
                new List[]{ pathList.get(0), pathList.get(1) };
        
        title("Result");
        
        System.out.println("Paths equal: " + listsEqual(pathListArray));
    }
    
    public static PuzzleGraphNode getSource(int steps, final Random rnd) {
        PuzzleGraphNode node = new PuzzleGraphNode(3);
        steps += steps % 2;
        
        while (steps > 0) {
            node = node.randomSwap(rnd);
            --steps;
        }
        
        return node;
    }
    
    public static void title(final String title) {
        if (title.length() >= SCREEN_WIDTH) {
            System.out.println(title);
            return;
        }
        
        final int left = (SCREEN_WIDTH - 2 - title.length()) / 2;
        final int right = SCREEN_WIDTH - left - 2 - title.length();
        final StringBuilder sb = new StringBuilder(SCREEN_WIDTH);
        
        if (left > 0) {
            for (int i = 0; i < left; ++i) {
                sb.append('-');
            }
        }
        
        sb.append(' ').append(title).append(' ');
        
        if (right > 0) {
            for (int i = 0; i < right; ++i) {
                sb.append('-');
            }
        }
        
        System.out.println(sb.toString());
    }
    
    public static <T> boolean listsEqual(final List<T>... lists) {
        for (int i = 0; i < lists.length - 1; ++i) {
            if (lists[i].size() != lists[i + 1].size()) {
                return false;
            }
        }
        
        for (int i = 0; i < lists[0].size(); ++i) {
            for (int lIndex1 = 0; lIndex1 < lists.length - 1; ++lIndex1) {
                for (int lIndex2 = lIndex1 + 1; 
                        lIndex2 < lists.length; 
                        ++lIndex2) {
                    
                    if (!lists[lIndex1].get(i).equals(lists[lIndex2].get(i))) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    private static List<PuzzleGraphNode> 
        profile(final AbstractPathFinder<PuzzleGraphNode> finder,
                final PuzzleGraphNode source,
                final PuzzleGraphNode target) {
        title(finder.getClass().getSimpleName());
        
        final long ta = System.currentTimeMillis();
        final List<PuzzleGraphNode> path = finder.search(source, target);
        final long tb = System.currentTimeMillis();
        
        System.out.println("Time: " + (tb - ta) + " ms. Path length: " + 
                           path.size());
        
        return path;
    }
}
