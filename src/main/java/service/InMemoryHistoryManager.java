package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodes = new HashMap<>();
    private Node head;
    private Node tail;

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = nodes.remove(id);
        removeNode(nodeToRemove);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task taskToAdd) {
        final Node newNode = new Node(taskToAdd);
        if (tail == null) {
            head = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
        }
        tail = newNode;
        nodes.put(taskToAdd.getId(), newNode);
    }

    private ArrayList<Task> getTasks() {
        final ArrayList<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.data);
            current = current.next;
        }
        return history;
    }

    private void removeNode(Node nodeToRemove) {
        if (nodeToRemove == null) {
            return;
        }

        final Node nodeAfter = nodeToRemove.next;
        final Node nodeBefore = nodeToRemove.prev;

        if (nodeBefore == null) {
            head = nodeAfter;
        } else {
            nodeBefore.next = nodeAfter;
            nodeToRemove.prev = null;
        }

        if (nodeAfter == null) {
            tail = nodeBefore;
        } else {
            nodeAfter.prev = nodeBefore;
            nodeToRemove.next = null;
        }
    }

    private static class Node {

        public Task data;
        public Node next;
        public Node prev;

        public Node(Task task) {
            this.data = task;
            this.next = null;
            this.prev = null;
        }
    }
}
