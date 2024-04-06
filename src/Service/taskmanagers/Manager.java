package Service.taskmanagers;

import Service.history.HistoryManager;
import Service.history.inMemoryHistoryManager;

public class Manager {
    public static TaskManager getDefaultTaskManager() {
        return new inMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new inMemoryHistoryManager();
    }
}
