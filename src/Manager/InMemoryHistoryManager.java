package Manager;

import entities.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager{
    private static List<Task> history = new ArrayList<>();
    @Override
    public<T extends Task> void addHistory(T task) {
        if (history.size() >= 10) {
            history.removeFirst();
        }
        history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
