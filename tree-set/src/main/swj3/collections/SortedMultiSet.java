package swj3.collections;

public interface SortedMultiSet<T extends Comparable<T>> extends Iterable<T> {
    void add(T elem);
    boolean contains(T elem);
    T get(T elem);
    int size();
    T first() throws EmptySetException;
    T last() throws EmptySetException;
}
