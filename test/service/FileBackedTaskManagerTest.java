package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.exception.ManagerSaveException;
import service.taskmanagers.FileBackedTaskManager;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FileBackedTaskManagerTest {
    protected Path file = Paths.get(System.getProperty("user.dir"), "data", "data.csv");
    protected FileBackedTaskManager fm;
    protected Task task;
    protected Task task1;
    protected SubTask subTask;
    protected SubTask subTask1;
    protected SubTask subTask2;
    protected Epic epic;
    protected Epic epic1;

    @BeforeEach
    void setUp() {
        fm = new FileBackedTaskManager(file);
        task = new Task("Обычная задача", "Первая", Status.NEW);
        task1 = new Task("Обычная задача", "Вторая", Status.NEW);
        subTask = new SubTask("Подзадача", "Первая подзадача", Status.NEW);
        subTask1 = new SubTask("Подзадача", "Вторая подзадача", Status.NEW);
        subTask2 = new SubTask("Подзадача", "Третья подзадача", Status.NEW);
        epic = new Epic("Эпик", "Первый эпик");
        epic1 = new Epic("Эпик", "Второй эпик");
    }

    @Test
    @DisplayName("Сохранения задачи и загрузка из файла")
    void saveTaskToFileAndLoadTaskFromFile() {
        fm.createTask(task);
        FileBackedTaskManager fileTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(task, fileTaskManager.getTask(0));
    }
    @Test
    @DisplayName("Проверка эпика при сохрании и загрузки файла")
    void saveBlankEpicToFileAndLoadEpicFromFile(){
        fm.createEpic(epic);
        FileBackedTaskManager fileTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(epic,fileTaskManager.getEpic(0));
    }
    @Test
    @DisplayName("Проверка подзадачи при сохрании и загрузки файла")
    void saveBlankSubTaskToFileAndLoadSubTaskFromFile(){
        fm.createEpic(epic);
        fm.createSubTask(subTask,epic.getId());
        FileBackedTaskManager fileTaskManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(subTask,fileTaskManager.getSubTask(1));
    }
    @Test
    @DisplayName("Проверка загрузки некоректного файла")
    void checkingTheDownloadOfAnIncorrectFile(){
        var throwex=assertThrows(ManagerSaveException.class,()->{
            FileBackedTaskManager.loadFromFile(Paths.get(System.getProperty("user.dir")));
        },"Путь не найден");
        assertEquals("Путь не найден", throwex.getMessage());
    }
}
