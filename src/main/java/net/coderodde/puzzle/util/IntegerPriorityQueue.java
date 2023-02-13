package net.coderodde.puzzle.util;

/**
 * This interface defines the API for minimum priority queues.
 * 
 * @author Rodion Efremov
 * @version 1.6
 * @param <T> the actual implementation type of this interface.
 * @param <E> the type of elements being stored in this heap.
 */
public interface IntegerPriorityQueue<T extends IntegerPriorityQueue<T, E>, E>
extends Spawnable<T> {
    
    /**
     * If <code>element</code> is not already present in the heap, inserts it.
     * 
     * @param element  the element to insert.
     * @param priority the priority of the element.
     */
    public void insert(final E element, final int priority);
    
    /**
     * Attempts to decrease the priority of element <code>element</code>. If the
     * new priority is no less than the current priority, the element is not 
     * moved within the heap.
     * 
     * @param element  the element whose priority to decrease.
     * @param priority the new priority.
     */
    public void decreasePriority(final E element, final int priority);
    
    /**
     * Removes the element with the least priority.
     * 
     * @return the minimum element.
     */
    public E extractMinimum();
    
    /**
     * Returns but does not remove the minimum element.
     * 
     * @return the minimum element.
     */
    public E min();
    
    /**
     * Returns the priority of the minimum element.
     * 
     * @return the minimum priority over this heap. 
     */
    public int minPriority();
    
    /**
     * Returns <code>true</code> if and only if this heap is empty.
     * 
     * @return <code>true</code> if the heap is empty.
     */
    public boolean isEmpty();
    
    /**
     * Returns the size of this heap.
     * 
     * @return the size.
     */
    public int size();
    
    /**
     * Clears the entire heap.
     */
    public void clear();
}
