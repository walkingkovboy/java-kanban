package Manager;

import entities.Task;

import java.util.List;

public interface HistoryManager {

    <T extends Task> void addHistory(T task);
    List<Task> getHistory();
}
