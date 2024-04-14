package server;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import service.taskmanagers.HttpTaskManager;
import service.taskmanagers.TaskManager;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTest {
    KVServer kvServer;
    protected Task task1;
    protected Task task2;
    protected SubTask subTask1;
    protected SubTask subTask2;
    protected SubTask subTask3;
    protected Epic epic1;
    protected Epic epic2;
    protected HttpTaskManager taskManager;


    @BeforeEach
    public void setUp() throws IOException, InterruptedException {
        kvServer = new KVServer();
        kvServer.start();
        taskManager = new HttpTaskManager("http://localhost");
        task1 = new Task("Обычная задача1", "Первая", Status.NEW);
        task2 = new Task("Обычная задача2", "Вторая", Status.NEW);
        subTask1 = new SubTask("Подзадача1", "Первая подзадача", Status.NEW);
        subTask2 = new SubTask("Подзадача2", "Вторая подзадача", Status.NEW);
        subTask3 = new SubTask("Подзадача3", "Третья подзадача", Status.NEW);
        epic1 = new Epic("Эпик1", "Первый эпик");
        epic2 = new Epic("Эпик2", "Второй эпик");
    }

    @AfterEach
    void afterEach() {
        kvServer.stop();
    }

    @Test
    public void saveTaskToServerAndLoadTaskFromServer() throws IOException, InterruptedException {
        setUp();
        taskManager.createTask(task1);
        TaskManager httpTaskManager = new HttpTaskManager("http://localhost");
        assertEquals(task1, httpTaskManager.getTask(task1.getId()));
        afterEach();
    }

    @Test
    public void saveBlankEpicToFileAndLoadEpicFromFile() throws IOException, InterruptedException {
        setUp();
        taskManager.createEpic(epic1);
        TaskManager httpTaskManager = new HttpTaskManager("http://localhost");
        assertEquals(epic1, httpTaskManager.getEpic(0));
        afterEach();
    }

}
