package net.coderodde.puzzle.util.support;

import net.coderodde.puzzle.util.IntegerPriorityQueue;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class DialHeapTest {
    
    private IntegerPriorityQueue<?, Integer> heap = new DialHeap<>();

    @Before
    public void init() {
        heap.clear();
    }
    
    @Test
    public void testAdd() {
        for (int i = 100; i != 0; --i) {
            heap.insert(i, i);
        }
        
        assertEquals((Integer) 1, heap.min());
        
        for (int i = 1; i != heap.size() + 1; ++i) {
            assertEquals((Integer) i, heap.extractMinimum());
        }
    }

    @Test
    public void testMin() {
        heap.insert(10, 10);
        
        assertEquals((Integer) 10, heap.min());
        
        heap.insert(11, 11);
        
        assertEquals((Integer) 10, heap.min());
        
        heap.insert(9, 9);
        
        assertEquals((Integer) 9, heap.min());
        
        heap.insert(1000, 8);
        
        assertEquals((Integer) 1000, heap.min());
    }

    @Test
    public void testDecreasePriority() {
        for (int i = 0; i != 1000; ++i) {
            heap.insert(i, 1000);
        }
        
        for (int i = 0; i != 1000; ++i) {
            heap.decreasePriority(i, i);
        }
        
        for (int i = 0; i != 1000; ++i) {
            assertEquals((Integer) i, heap.extractMinimum());
        }
        
        assertEquals(0, heap.size());
    }

    @Test
    public void testSize() {
        for (int i = 0; i != 1000; ++i) {
            assertEquals(i, heap.size());
            heap.insert(i, i);
        }
        
        heap.clear();
        
        assertEquals(0, heap.size());
        
        heap.clear();
        
        assertEquals(0, heap.size());
    }
}