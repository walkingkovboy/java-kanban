import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import manager.FileBackedTaskManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

    @DisplayName("Проверка сохранения задач")
    @Test
    public void testSaveFile() throws IOException {
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager();
        Task task1 = new Task("Задача 1", "Описание задачи", Status.NEW);
        manager.addTask(task1);
        Epic epic1 = new Epic("Эпик 1", "Описание эпика");
        manager.addEpic(epic1);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание подзадачи", Status.NEW);
        manager.addSubtask(subTask1, epic1.getId());
        assertEquals(1, manager.getTasks().size());
        assertEquals(1, manager.getEpics().size());
        assertEquals(1, manager.getSubtasks().size());
    }

    @DisplayName("Проверка загрузки задач")
    @Test
    public void testLoadFile() throws IOException {
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        String csvContent = String.join("\n",
                "id,type,name,status,description,startTime,duration,epic",
                "0,TASK,Задача 1,NEW,Описание задачи,null,null,null",
                "1,EPIC,Эпик 1,NEW,Описание эпика,null,null,null",
                "2,SUBTASK,Подзадача 1,NEW,Описание подзадачи,null,null,1"
        );
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(csvContent);
        }
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, manager.getTasks().size());
        assertEquals(1, manager.getEpics().size());
        assertEquals(1, manager.getSubtasks().size());
    }

    @DisplayName("Проверка загрузки пустого файла")
    @Test
    public void testLoadEmptyFile() throws IOException {
        File emptyFile = File.createTempFile("empty_tasks", ".csv");
        emptyFile.deleteOnExit();
        FileBackedTaskManager manager = FileBackedTaskManager.loadFromFile(emptyFile);
        assertTrue(manager.getTasks().isEmpty(), "Список задач должен быть пуст");
        assertTrue(manager.getEpics().isEmpty(), "Список эпиков должен быть пуст");
        assertTrue(manager.getSubtasks().isEmpty(), "Список подзадач должен быть пуст");
    }
}
