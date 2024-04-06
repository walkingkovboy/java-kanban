package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.history.InMemoryHistoryManager;
import service.taskmanagers.Manager;
import service.taskmanagers.TaskManager;
import org.junit.jupiter.api.BeforeEach;

class inMemoryHistoryManagerTest extends HistoryManagerTest<InMemoryHistoryManager>  {
    Epic epic;
    SubTask subTask;
    TaskManager tm = Manager.getDefaultTaskManager();

    @BeforeEach
    public void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Task1", "Task1Description",Status.NEW,1);
        task2 = new Task("Task2", "Task2Description",Status.NEW,2);
        task3 = new Task("Task2", "Task2Description",Status.NEW,3);
    }
}