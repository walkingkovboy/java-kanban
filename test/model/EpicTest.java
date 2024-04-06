package model;

import service.taskmanagers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private InMemoryTaskManager taskManager;
    private Epic epic;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager();
        epic = new Epic("NameEpic", "EpicDescription");
        epic = taskManager.createEpic(epic);
    }

    @DisplayName("Статус должен совпасть")
    @Test
    public void epicStatusNewWhenNoSubTasks() {
        assertEquals(Status.NEW, epic.getStatus());
    }

    @DisplayName("Статус должен быть NEW")
    @Test
    public void epicStatusNewWhenAllSubTasksNew() {
        SubTask subTask1 = new SubTask("SubTask1", "Subtask1Description", Status.NEW);
        SubTask subTask2 = new SubTask("SubTask2", "Subtask2Description", Status.NEW);
        taskManager.createSubTask(subTask1, epic.getId());
        taskManager.createSubTask(subTask2, epic.getId());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    @DisplayName("Статус должен быть DONE")
    public void epicStatusDoneWhenAllSubTasksDone() {
        SubTask subTask1 = new SubTask("SubTask1", "Subtask1Description", Status.DONE);
        SubTask subTask2 = new SubTask("SubTask2", "Subtask2Description", Status.DONE);
        taskManager.createSubTask(subTask1, epic.getId());
        taskManager.createSubTask(subTask2, epic.getId());
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    @DisplayName("Статус должен быть IN_PROGRESS")
    public void epicStatusInProgressWhenSomeSubTasksDoneOrNew() {
        SubTask subTask1 = new SubTask("SubTask1", "Subtask1Description", Status.DONE);
        SubTask subTask2 = new SubTask("SubTask2", "Subtask2Description", Status.IN_PROGRESS);
        taskManager.createSubTask(subTask1, epic.getId());
        taskManager.createSubTask(subTask2, epic.getId());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Статус должен быть IN_PROGRESS")
    public void epicStatusInProgressWhenAllSubTasksInProgress() {
        SubTask subTask1 = new SubTask("SubTask1", "Subtask1Description", Status.IN_PROGRESS);
        SubTask subTask2 = new SubTask("SubTask2", "Subtask2Description", Status.IN_PROGRESS);
        taskManager.createSubTask(subTask1, epic.getId());
        taskManager.createSubTask(subTask2, epic.getId());
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    @DisplayName("Айди в ХешМапе и айди у самого Эпика должен совпасть")
    public void epicIdMatchWithEpicHashMapId() {
        assertEquals(epic.getId(), taskManager.getEpic(0).getId());
    }

}