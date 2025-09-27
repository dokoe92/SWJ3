package swj3.collections;

import java.util.*;

public class BSTMultiSet<T extends Comparable<T>> implements SortedMultiSet<T> {

    private static class Node<T> {
        private T value;
        private Node<T> left, right;

        public Node(T val, Node<T> left, Node<T> right) {
            this.value = val;
            this.left = left;
            this.right = right;
        }
    }

    private Node<T> root;
    private int size;

    public BSTMultiSet() {
        root = null;
        size = 0;
    }

    @Override
    public void add(T elem) {
        root = addRecursive(root, elem);
    }

    private Node<T> addRecursive(Node<T> parent, T elem) {
        if (parent == null) {
            size++;
            return new Node<>(elem, null, null);
        }

        if (elem.compareTo(parent.value) < 0) {
            parent.left = addRecursive(parent.left, elem);
        } else {
            parent.right = addRecursive(parent.right, elem);
        }

        return parent;
    }

    @Override
    public boolean contains(T elem) {
        return get(elem) != null;
    }

    @Override
    public T get(T elem) {
        Node<T> t = root;
        while (t != null) {
            int cmp = t.value.compareTo(elem);
            if (cmp == 0) {
                return t.value;
            } else if (cmp > 0) {
                t = t.left;
            } else {
                t = t.right;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new BSTIterator<>(root);
    }

    private static class BSTIterator<T> implements Iterator<T> {
        private final Deque<Node<T>> unvisitedParents = new ArrayDeque<>();

        // initially move to the left bottom of tree
        public BSTIterator(Node<T> root) {
            Node<T> next = root;
            while (next != null) {
                unvisitedParents.push(next);
                next = next.left;
            }
        }

        @Override
        public boolean hasNext() {
            return !unvisitedParents.isEmpty();
        }

        // from left bottom of tree move further
        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Node<T> cur = unvisitedParents.pop();
            Node<T> next = cur.right;
            while (next != null) {
                unvisitedParents.push(next);
                next = next.left;
            }
            return cur.value;
        }
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        traverseInOrder(root, sj);
        return sj.toString();
    }

    private void traverseInOrder(Node<T> t, StringJoiner sj) {
        if (t != null) {
            traverseInOrder(t.left, sj);
            sj.add(t.value.toString());
            traverseInOrder(t.right, sj);
        }
    }

    @Override
    public T first() throws EmptySetException {
        if (root == null) throw new EmptySetException("first is undefined for empty set");

        Node<T> t = root;
        while (t.left != null) {
            t = t.left;
        }
        return t.value;
    }

    @Override
    public T last() throws EmptySetException {
        if (root == null) throw new EmptySetException("last is undefined for empty set");

        Node<T> t = root;
        while (t.right != null) {
            t = t.right;
        }
        return t.value;
    }

}
