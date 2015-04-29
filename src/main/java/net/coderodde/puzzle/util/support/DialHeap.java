package net.coderodde.puzzle.util.support;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import net.coderodde.puzzle.util.IntegerPriorityQueue;

/**
 * Implements Dial's heap.
 * 
 * @author Rodion Efremov
 * @version 1.6
 * @param <E> the type of elements stored by this heap.
 */
public class DialHeap<E> implements IntegerPriorityQueue<DialHeap<E>, E> {

    /**
     * The list node in the bucket.
     * 
     * @param <E> the type of actual element being stored.
     */
    private static class ListNode<E> {
        
        /**
         * The element.
         */
        E element;
        
        /**
         * The priority of <code>element</code>.
         */
        int priority;
        
        /**
         * The predecessor node.
         */
        ListNode<E> prev;
        
        /**
         * The successor node.
         */
        ListNode<E> next;
        
        /**
         * Constructs a new <code>ListNode</code> with specified element and 
         * priority.
         * 
         * @param element  the element to store.
         * @param priority the priority of the element.
         */
        ListNode(final E element, final int priority) {
            this.element = element;
            this.priority = priority;
        }
    }
    
    /**
     * The default capacity of the storage array.
     */
    private static final int DEFAULT_CAPACITY = 256;
    
    /**
     * Maps each stored element to the list node it is in.
     */
    private final Map<E, ListNode<E>> map;
    
    /**
     * The actual storage array. The index is effectively a priority value, and
     * the component at that index is a list of elements with that particular
     * priority.
     */
    private ListNode<E>[] storage;
    
    /**
     * The amount of elements in this priority queue.
     */
    private int size;
    
    /**
     * Caches the minimal priority value.
     */
    private int minimumPriority = Integer.MAX_VALUE;
    
    /**
     * Constructs a new Dial's heap.
     */
    public DialHeap() {
        this.storage = new ListNode[DEFAULT_CAPACITY];
        this.map = new HashMap<>();
    }
    
    /**
     * {@inheritDoc }
     * 
     * @param element  the element to store.
     * @param priority the priority of <code>element</code>.
     */
    @Override
    public void insert(E element, int priority) {
        if (map.containsKey(element)) {
            return;
        }
        
        ensurePriority(priority);
        
        ListNode<E> node = new ListNode<>(element, priority);
        map.put(element, node);
        ++size;
        
        if (storage[priority] != null) {
            storage[priority].prev = node;
            node.next = storage[priority];
        }
        
        storage[priority] = node;
        
        if (minimumPriority > priority) {
            minimumPriority = priority;
        }
    }

    /**
     * {@inheritDoc }
     * 
     * @param element  the target element.
     * @param priority the new priority.
     */
    @Override
    public void decreasePriority(E element, int priority) {
        final ListNode<E> node = map.get(element);
        
        if (node == null || node.priority <= priority) {
            return;
        }
        
        if (node.prev != null) {
            node.prev.next = node.next;
        }
        
        if (node.next != null) {
            node.next.prev = node.prev;
        }
        
        if (storage[node.priority] == node) {
            storage[node.priority] = node.next;
        }
        
        node.priority = priority;
        
        if (storage[priority] != null) {
            storage[priority].prev = node;
            node.next = storage[priority];
        }
        
        storage[priority] = node;
        
        if (minimumPriority > priority) {
            minimumPriority = priority;
        }
    }

    @Override
    public E extractMinimum() {
        checkNotEmpty();
        
        final ListNode<E> node = storage[minimumPriority];
        map.remove(node.element);
        --size;
        
        if (node.next != null) {
            node.next.prev = null;
            storage[minimumPriority] = node.next;
            return node.element;
        }
    
        storage[minimumPriority] = null;

        for (int i = minimumPriority + 1; i != storage.length; ++i) {
            if (storage[i] != null) {
                minimumPriority = i;
                return node.element;
            }
        }

        minimumPriority = Integer.MAX_VALUE;
        return node.element;
    }

    @Override
    public E min() {
        checkNotEmpty();
        return ((ListNode<E>) storage[minimumPriority]).element;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        for (int i = minimumPriority; i < storage.length; ++i) {
            storage[i] = null;
        }
        
        map.clear();
        size = 0;
    }

    @Override
    public DialHeap<E> spawn() {
        return new DialHeap<>();
    }
    
    private final void checkNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("This queue is empty.");
        }
    }
    
    private final void ensurePriority(final int requestedPriority) {
        if (storage.length <= requestedPriority) {
            final ListNode[] newStorage = 
                    new ListNode[Math.max(2 * storage.length, 
                                          requestedPriority + 1)];
            
            System.arraycopy(storage, 0, newStorage, 0, storage.length);
            storage = newStorage;
        }
    }
}
