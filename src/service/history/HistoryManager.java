package service.history;

import model.Task;

import java.util.List;

public interface HistoryManager {
void addTaskHistory(Task task);
 void remove(int id);
 List<Task> getHistory();
}
