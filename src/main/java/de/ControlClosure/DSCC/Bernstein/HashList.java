package de.ControlClosure.DSCC.Bernstein;

import java.util.*;

public class HashList<T> implements Iterable<T>  {

    private class Node<T> {
        T value;
        Node<T> next;
        Node<T> prev;

        Node(T value) {
            this.value = value;
        }
    }

    private final Map<T, Node<T>> nodeMap = new HashMap<>();
    private Node<T> head;
    private Node<T> tail;


    public int size() {
        return nodeMap.size();
    }

    public void addLast(T value) {
        Node<T> node = new Node<>(value);
        nodeMap.put(value, node);

        if (head == null) {
            head = tail = node;
            return;
        }

        tail.next = node;
        node.prev = tail;
        tail = node;
    }

    public void addFirst(T value) {
        Node<T> node = new Node<>(value);
        nodeMap.put(value, node);

        if (head == null) {
            head = tail = node;
            return;
        }

        node.next = head;
        head.prev = node;
        head = node;
    }

    public void addAllLast(List<T> values) {
        for(T value: values) {
            addLast(value);
        }
    }

    public void insertAfterMultiple(T target, List<T> values) {
        T currentTarget = target;
        for(T value: values) {
            insertAfter(currentTarget, value);
            currentTarget = value;
        }
    }

    public void insertAfter(T target, T value) {
        Node<T> current = nodeMap.get(target);
        if (current == null) return;

        Node<T> node = new Node<>(value);
        nodeMap.put(value, node);

        node.prev = current;
        node.next = current.next;

        if (current.next != null) {
            current.next.prev = node;
        } else {
            tail = node;
        }

        current.next = node;
    }

    public void insertBefore(T target, T value) {
        Node<T> current = nodeMap.get(target);
        if (current == null) return;

        Node<T> node = new Node<>(value);
        nodeMap.put(value, node);

        node.next = current;
        node.prev = current.prev;

        if (current.prev != null) {
            current.prev.next = node;
        } else {
            head = node;
        }

        current.prev = node;
    }

    public void remove(T value) {
        Node<T> node = nodeMap.get(value);
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        nodeMap.remove(value);
        // Help GC
        node.next = null;
        node.prev = null;
    }

    public void replace(T target, List<T> values) {
        Node<T> nodeToReplace = nodeMap.get(target);
        Node<T> prev = nodeToReplace.prev;
        remove(target);

        if (prev == null) {
            addFirst(values.get(0));
            for(int i = 1; i < values.size(); i++) {
                insertAfter(values.get(i-1),values.get(i));
            }
        } else {
            insertAfterMultiple(prev.value, values);
        }
    }

    public T prev(T current) {
        if (current == null) return tail.value;

        Node<T> node = nodeMap.get(current);
        Node<T> prev = node.prev;

        if (prev == null) return null;

        return node.prev.value;
    }

    public T last() {
        return tail.value;
    }

    public List<T> toList() {
        List<T> result = new ArrayList<>();

        Node<T> current = head;
        while (current != null) {
            result.add(current.value);
            current = current.next;
        }

        return result;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {

            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException();
                }
                T value = current.value;
                current = current.next;
                return value;
            }
        };
    }
}
