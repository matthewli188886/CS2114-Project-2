package hotel;

/**
 * A generic singly linked list that keeps elements in insertion order.
 * Elements are stored by reference and null is never stored.
 *
 * Invariants (must hold after every method call):
 * - empty: head == null, tail == null, size == 0
 * - non-empty: head and tail are non-null, tail.next == null,
 *   size == number of nodes reachable from head
 * - one element: head == tail
 *
 * @param <T> the element type (no bound)
 */
public class LinkedChain<T> {

    private static class Node<T> {
        private T data;
        private Node<T> next;
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    /** Creates an empty chain. */
    public LinkedChain() {
        // TODO
    }

    /** Adds to the end. O(1). Throws IllegalArgumentException if item is null. */
    public void add(T item) {
        // TODO
    }

    /** Removes the first match found with equals(). Returns false if absent. O(n). */
    public boolean remove(T item) {
        // TODO
        return false;
    }

    /** O(n). */
    public boolean contains(T item) {
        // TODO
        return false;
    }

    /** O(n). Throws IndexOutOfBoundsException if index < 0 or index >= size. */
    public T get(int index) {
        // TODO
        return null;
    }

    /** O(1). */
    public int size() {
        // TODO
        return 0;
    }

    /** O(1). */
    public boolean isEmpty() {
        // TODO
        return false;
    }
}
