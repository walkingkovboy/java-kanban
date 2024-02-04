package Service;

import Model.Task;

import java.util.List;

public interface HistoryManager {
    public void addTaskHistory(Task task);

    public List<Task> getAll();
}
