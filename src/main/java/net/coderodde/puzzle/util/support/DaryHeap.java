package net.coderodde.puzzle.util.support;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import net.coderodde.puzzle.util.IntegerPriorityQueue;

/**
 * This class implements a <tt>d</tt>-ary (min priority) heap, where <tt>d</tt>
 * is the branching factor of the heap, and is usually called a <b>degree</b> of
 * a heap.
 * 
 * @author Rodion Efremov
 * @version 1.6
 * @param <E> the type of elements stored in this heap
 */
public class DaryHeap<E> implements IntegerPriorityQueue<DaryHeap<E>, E> {

    /**
     * The structure for storing the elements in this heap.
     * 
     * @param <E> the actual type of the data to be stored.
     */
    private static final class Node<E> {
        
        Node(final E element,
             final int index,
             final int priority) {
            this.element = element;
            this.index = index;
            this.priority = priority;
        }
        
        /**
         * The actual element being stored.
         */
        final E element;
        
        /**
         * The index at which this <code>Node</code> resides in the storage 
         * array.
         */
        int index;
        
        /**
         * The priority of element.
         */
        int priority;
    }
    
    /**
     * The minimum capacity of the storage array.
     */
    private static final int MINIMUM_CAPACITY = 128;
    
    /**
     * The default capacity of the storage array.
     */
    private static final int DEFAULT_CAPACITY = 1024;
    
    /**
     * The default degree of this heap. This is also minimum degree.
     */
    private static final int DEFAULT_DEGREE = 2;
    
    private static final int NO_CHILD = -1;
    
    /**
     * The degree of this heap.
     */
    private final int degree;
    
    /**
     * The storage array.
     */
    private Object[] storage;
    
    /**
     * Used to keep the decrease operation an O(log N) operation.
     */
    private final Map<E, Node<E>> map;
    
    /**
     * Used to avoid creating index arrays every time we need to find out the 
     * indices of the child nodes.
     */
    private final int[] indices;
    
    /**
     * Caches the amount of elements stored in this heap.
     */
    private int size;

    /**
     * Constructs a new <code>degree</code>-ary heap with initial capacity 
     * <code>capacity</code>.
     * 
     * @param degree   the degree of this heap.
     * @param capacity the capacity of the underlying storage array.
     */
    public DaryHeap(final int degree, int capacity) {
        checkDegree(degree);
        capacity = fixCapacity(capacity);
        
        this.degree = degree;
        this.storage = new Object[capacity];
        this.indices = new int[degree];
        this.map = new HashMap<>(capacity);
    }
    
    /**
     * Constructs a new heap with degree <code>degree</code> and default initial
     * capacity.
     * 
     * @param degree the degree of the new heap.
     */
    public DaryHeap(final int degree) {
        this(degree, DEFAULT_CAPACITY);
    }
    
    /**
     * Constructs a new binary heap with default capacity.
     */
    public DaryHeap() {
        this(DEFAULT_DEGREE);
    }
    
    /**
     * {@inheritDoc }
     * 
     * @param element  the element to insert.
     * @param priority the priority of the new element.
     */
    @Override
    public void insert(final E element, int priority) {
        if (map.containsKey(element)) {
            return;
        }
        
        ensureCapacity(size + 1);
        
        Node<E> node = new Node<>(element, size, priority);
        storage[size] = node;
        map.put(element, node);
        siftUp(size++);
    }

    /**
     * {@inheritDoc }
     * 
     * @param element  the element whose priority to decrease.
     * @param priority the new priority.
     */
    @Override
    public void decreasePriority(final E element, int priority) {
        final Node<E> node = map.get(element);
        
        if (node == null || node.priority <= priority) {
            return;
        }
        
        node.priority = priority;
        siftUp(node.index);
    }

    /**
     * {@inheritDoc }
     * 
     * @return the element with the least priority.
     */
    @Override
    public E extractMinimum() {
        checkNotEmpty();
        final E ret = ((Node<E>) storage[0]).element;
        
        map.remove(ret);
        Node<E> node = (Node<E>) storage[--size];
        storage[size] = null;
        
        if (size != 0) {
            storage[0] = node;
            node.index = 0;
            siftDown(0);
        }
        
        return ret;
    }
    
    @Override
    public E min() {
        checkNotEmpty();
        
        return ((Node<E>) storage[0]).element;
    }

    /**
     * {@inheritDoc }
     * 
     * @return <code>true</code> if and only if this heap is empty.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    public int size() {
        return size;
    }
    
    public void clear() {
        for (int i = 0; i < size; ++i) {
            storage[i] = null;
        }
        
        size = 0;
        map.clear();
    }

    /**
     * Returns another, empty {@code DaryHeap} with the same degree as this
     * heap.
     * 
     * @return a new empty heap.
     */
    @Override
    public DaryHeap<E> spawn() {
        return new DaryHeap<>(degree);
    }
    
    /**
     * Checks that the degree is no less than two.
     * 
     * @param degree the degree to check. 
     */
    private final void checkDegree(final int degree) {
        if (degree < 2) {
            throw new IllegalArgumentException(
                    "The requested degree is too small: " + degree + 
                    ", should be at least " + DEFAULT_DEGREE);
        }
    }
    
    private final void checkNotEmpty() {
        if (size == 0) {
            throw new NoSuchElementException("The heap is empty.");
        }
    }
    
    /**
     * Makes sure that the capacity is no less than the minimum capacity.
     * 
     * @param  capacity the capacity to check.
     * @return fixed capacity.
     */
    private final int fixCapacity(final int capacity) {
        return Math.max(MINIMUM_CAPACITY, capacity);
    }
    
    /**
     * Makes sure that the storage array can accommodate 
     * <code>requestedCapacity</code> amount of elements.
     * 
     * @param requestedCapacity the requested capacity.
     */
    private final void ensureCapacity(final int requestedCapacity) {
        if (storage.length < requestedCapacity) {
            final Object[] newStorage = new Object[2 * storage.length];
            System.arraycopy(storage, 0, newStorage, 0, size);
            storage = newStorage;
        }
    }
    
    /**
     * Returns the index of a node stored at index <code>index</code>.
     * 
     * @param  index the index of the node whose parent's index we want to
     *               compute.
     * @return       the index of the parent node.
     */
    private final int getParentIndex(final int index) {
        return (index - 1) / degree;
    }
    
    /**
     * Computes the indices of all the child nodes of the node at index 
     * <code>index</code>.
     * 
     * @param index the index of the node whose child nodes' indices to compute.
     */
    private final void computeChildIndices(final int index) {
        for (int i = 0; i != degree; ++i) {
            indices[i] = degree * index + i + 1;
            
            if (indices[i] >= size) {
                indices[i] = NO_CHILD;
                return;
            }
        }
    }
    
    /**
     * Sifts a node at index <code>index</code> until minimum heap invariant is
     * fixed.
     * 
     * @param index the index of a node to sift up.
     */
    private final void siftUp(int index) {
        int parentIndex = getParentIndex(index);
        final Node<E> target = (Node<E>) storage[index];
        
        while (index != 0) {
            Node<E> parent = (Node<E>) storage[parentIndex];
            
            if (parent.priority > target.priority) {
                storage[index] = parent;
                parent.index = index;
                
                index = parentIndex;
                parentIndex = getParentIndex(index);
            } else {
                break;
            }
        }
        
        storage[index] = target;
        target.index = index;
    }
    
    /**
     * Sifts a node down the heap until minimum heap invariant is satisfied.
     * 
     * @param index the index of the node to sift down.
     */
    private final void siftDown(int index) {
        final Node<E> target = (Node<E>) storage[index];
        final int targetPriority = target.priority;
        
        for (;;) {
            int minChildPriority = targetPriority;
            int minChildIndex = NO_CHILD;
            computeChildIndices(index);
            
            for (final int i : indices) {
                if (i == NO_CHILD) {
                    break;
                }
                
                final int tentativePriority = ((Node<E>) storage[i]).priority;
                
                if (minChildPriority > tentativePriority) {
                    minChildPriority = tentativePriority;
                    minChildIndex = i;
                }
            }
            
            if (minChildIndex == NO_CHILD) {
                storage[index] = target;
                target.index = index;
                return;
            }
            
            storage[index] = storage[minChildIndex];
            ((Node<E>) storage[index]).index = index;
            
            // Go for the next iteration.
            index = minChildIndex;
        }
    } 
}
