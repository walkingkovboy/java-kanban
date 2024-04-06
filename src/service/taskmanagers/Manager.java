package service.taskmanagers;

import service.history.HistoryManager;
import service.history.inMemoryHistoryManager;

public class Manager {
    public static TaskManager getDefaultTaskManager() {
        return new inMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new inMemoryHistoryManager();
    }
}
