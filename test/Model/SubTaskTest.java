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
        epic = taskManager.createEpic(new Epic(new Task("NameEpic", "EpicDescription", Status.NEW), new ArrayList<SubTask>()));
        subTask = taskManager.createSubTask(new SubTask(new Task("SubTask1", "SubTask1Description", Status.NEW), epic));
    }

    @Test
    @DisplayName("Эпики должны совпасть")
    public void subTaskGetEpic(){
        assertEquals(subTask.getEpic(),epic);
    }
    @Test
    @DisplayName("Удалим Эпик и должны получить NULL")
    public void subTaskDeleteEpic(){
        taskManager.removeEpic(epic.getId());
        assertEquals(null ,taskManager.getSubTask(subTask.getId()).getEpic());
    }
}