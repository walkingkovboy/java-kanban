package manager;

import entities.Node;
import entities.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> HistoryMap = new HashMap<>();
    private Node head;
    private Node tail;


    @Override
    public void addHistory(Task task) {
        if (HistoryMap.containsKey(task.getId())) {
            removeNode(HistoryMap.get(task.getId()));
        }
        Node newNode = new Node(task);
        linkLast(newNode);
        HistoryMap.put(task.getId(), newNode);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }
        return history;
    }

    private void linkLast(Node node) {
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    private void removeNode(Node node) {
        Node prevNode = node.prev;
        Node nextNode = node.next;
        if (prevNode != null) {
            prevNode.next = nextNode;
        } else {
            head = nextNode;
        }
        if (nextNode != null) {
            nextNode.prev = prevNode;
        } else {
            tail = prevNode;
        }
        node.prev = null;
        node.next = null;
        HistoryMap.remove(node.task.getId());
    }

    @Override
    public void remove(int id) {
        Node node = HistoryMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }
}
