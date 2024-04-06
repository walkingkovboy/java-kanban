package Service.history;

import Model.Task;

import java.util.List;

public interface HistoryManager {
void addTaskHistory(Task task);
 void remove(int id);
 List<Task> getHistory();
}
