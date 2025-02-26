package app.service;

import app.entities.Task;
import app.interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final CustomLinkedList history;

    public InMemoryHistoryManager() {
        history = new CustomLinkedList();
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            // Не добавляем null задачи
            return;
        }
        history.removeNode(task.getId());
        history.linkLast(task);
    }

    @Override
    public void remove(int id) {
        history.removeNode(id);
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    private static class Node<T> {
        public T value;
        public Node<T> next;
        public Node<T> prev;

        public Node(Node<T> prev, T value, Node<T> next) {
            this.value = value;
            this.next = next;
            this.prev = prev;
        }
    }

    private static class CustomLinkedList {
        private Node<Task> head;
        private Node<Task> tail;

        private final Map<Integer, Node<Task>> nodes = new HashMap<>();

        private void linkLast(Task task) {
            final Node<Task> newNode = new Node<>(tail, task, null);
            if (tail == null) {
                head = newNode;
            } else {
                tail.next = newNode;
            }
            tail = newNode;
            nodes.put(task.getId(), newNode);
        }

        private void removeNode(int id) {
            Node<Task> node = nodes.remove(id);
            if (node == null) {
                return;
            }
            removeNode(node);
        }

        private void removeNode(Node<Task> removedNode) {
            final Node<Task> prev = removedNode.prev;
            final Node<Task> next = removedNode.next;
            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
            }
            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
            }
            nodes.remove(removedNode.value.getId());
        }

        private List<Task> getTasks() {
            List<Task> tasks = new ArrayList<>();
            for (Node<Task> node = head; node != null; node = node.next) {
                tasks.add(node.value);
            }
            return tasks;
        }
    }
}
