package Model;

import Service.inMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    private inMemoryTaskManager taskManager;
    private SubTask subTask;
    private Epic epic;

    @BeforeEach
    public void setUp() {
        taskManager = new inMemoryTaskManager();
        epic = new Epic("NameEpic", "EpicDescription");
        SubTask subTask1 = new SubTask("SubTask1", "Subtask1Description", Status.NEW);
        epic = taskManager.createEpic(epic);
        subTask = taskManager.createSubTask(subTask1, epic.getId());
    }

    @Test
    @DisplayName("Эпики должны совпасть")
    public void subTaskGetEpic() {
        assertEquals(subTask.getEpic(), epic);
    }

    @Test
    @DisplayName("Удалим Эпик и должны получить NULL")
    public void subTaskDeleteEpic() {
        taskManager.removeEpic(epic.getId());
        assertEquals(null, taskManager.getSubTask(subTask.getId()).getEpic());
    }
}