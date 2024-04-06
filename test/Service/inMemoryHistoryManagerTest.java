package Service;

import Model.Epic;
import Model.Status;
import Model.SubTask;
import Model.Task;
import Service.history.inMemoryHistoryManager;
import Service.taskmanagers.Manager;
import Service.taskmanagers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class inMemoryHistoryManagerTest extends HistoryManagerTest<inMemoryHistoryManager>  {
    Epic epic;
    SubTask subTask;
    TaskManager tm = Manager.getDefaultTaskManager();

    @BeforeEach
    public void setUp() {
        historyManager = new inMemoryHistoryManager();
        task1 = new Task("Task1", "Task1Description",Status.NEW,1);
        task2 = new Task("Task2", "Task2Description",Status.NEW,2);
        task3 = new Task("Task2", "Task2Description",Status.NEW,3);
    }
}