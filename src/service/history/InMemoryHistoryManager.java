package service.history;

import model.Task;
import service.linkedList.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private HashMap<Integer, Node<Task>> mapForRemoving = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    @Override
    public void addTaskHistory(Task task) {
        if (task == null) {
            return;
        }
        Integer taskId = task.getId();
        if (mapForRemoving.containsKey(taskId)) {
            removeNode(mapForRemoving.get(taskId));
        }
        Node<Task> node = linkLast(task);
        mapForRemoving.put(taskId, node);
    }

    @Override
    public void remove(int id) {
        removeNode(mapForRemoving.get(id));
        mapForRemoving.remove(id);
    }

    private void removeNode(Node<Task> node) {
        Node<Task> prevNode = node.getPrev();
        Node<Task> nextNode = node.getNext();
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
    }

    private Node<Task> linkLast(Task task) {
        Node<Task> oldTail = tail;
        Node<Task> newNode = new Node<>(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        return newNode;
    }

    private List<Task> getTasks() {
        Node<Task> node = head;
        List<Task> elems = new ArrayList<>();
        while (node != null) {
            elems.add(node.getData());
            node = node.getNext();
        }
        return elems;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }
}
