package app.entities;

public class Node<T> {
    public T value;
    public Node<T> next;
    public Node<T> prev;

    public Node(Node<T> prev, T value, Node<T> next) {
        this.value = value;
        this.next = next;
        this.prev = prev;
    }
}
