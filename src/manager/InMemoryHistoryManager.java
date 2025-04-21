package manager;

import entities.Node;
import entities.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private final Map<Integer, Node> historyMap = new HashMap<>();
    private Node head;
    private Node tail;


    @Override
    public void addHistory(Task task) {
        if (historyMap.containsKey(task.getId())) {
            removeNode(historyMap.get(task.getId()));
        }
        Node newNode = new Node(task);
        linkLast(newNode);
        historyMap.put(task.getId(), newNode);
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.getTask());
            current = current.getNext();
        }
        return history;
    }

    private void linkLast(Node node) {
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            tail.setNext(node);
            node.setPrev(tail);
            tail = node;
        }
    }

    private void removeNode(Node node) {
        Node prevNode = node.getPrev();
        Node nextNode = node.getNext();
        if (prevNode != null) {
            prevNode.setNext(nextNode);
        } else {
            head = nextNode;
        }
        if (nextNode != null) {
            nextNode.setPrev(prevNode);
        } else {
            tail = prevNode;
        }
        node.setPrev(null);
        node.setNext(null);
        historyMap.remove(node.getTask().getId());
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }
}
