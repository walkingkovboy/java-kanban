
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @DisplayName("Проверка добавления двух задач")
    @Test
    void testAddTask() {
        taskManager.addTask(new Task("Задача 1", "Описание 1 задачи", Status.DONE));
        taskManager.addTask(new Task("Задача 2", "Описание 2 задачи", Status.IN_PROGRESS));

        assertEquals(2, taskManager.getTasks().size());
    }

    @DisplayName("Проверка созданного Эпика и Эпика в мапе")
    @Test
    void testAddEpicToItself() {
        Epic epic = new Epic("Эпик1", "Описание первого эпика");
        taskManager.addEpic(epic);
        assertEquals(epic.getClass(), taskManager.getEpic(0).getClass());
    }

    @DisplayName("Проверка созданого таска и таска в мапе")
    @Test
    void testAddTaskToItself() {
        Task task = new Task("Задача 1", "Описание 1 задачи", Status.DONE);
        taskManager.addTask(task);
        assertEquals(task.getClass(), taskManager.getTask(0).getClass());
    }

    @DisplayName("Проверка созданного сабтаска и сабтаска в мапе")
    @Test
    void testAddSubTaskToItself() {
        SubTask subTask = new SubTask("Подзадача3", "Описание 3 подзадачи", Status.DONE);
        Epic epic = new Epic("Эпик1", "Описание первого эпика");
        taskManager.addEpic(epic);
        taskManager.addSubtask(subTask, 0);
        assertEquals(subTask.getClass(), taskManager.getSubtask(1).getClass());
    }

    @DisplayName("Проверка статуса созданого эпика без подзадач")
    @Test
    void testEpicStatusNew() {
        Epic epic = new Epic("Эпик1", "Описание первого эпика");
        taskManager.addEpic(epic);
        assertEquals(Status.NEW, taskManager.getEpic(0).getStatus());
    }

    @DisplayName("Проверка статуса созданого эпика c подзадачми IN PROGRESS")
    @Test
    void testEpicStatusINPROGRESS() {
        Epic epic = new Epic("Эпик1", "Описание первого эпика");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Подзадача3", "Описание 3 подзадачи", Status.DONE);
        SubTask subTask1 = new SubTask("Подзадача3", "Описание 3 подзадачи", Status.IN_PROGRESS);
        SubTask subTask2 = new SubTask("Подзадача3", "Описание 3 подзадачи", Status.DONE);
        taskManager.addSubtask(subTask, 0);
        taskManager.addSubtask(subTask1, 0);
        taskManager.addSubtask(subTask2, 0);
        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(0).getStatus());
    }

    @DisplayName("Проверка статуса созданого эпика c подзадачами, при обновлении статуса подзадач DONE")
    @Test
    void testEpicStatusDONE() {
        Epic epic = new Epic("Эпик1", "Описание первого эпика");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Подзадача3", "Описание 3 подзадачи", Status.DONE);
        SubTask subTask1 = new SubTask("Подзадача3", "Описание 3 подзадачи", Status.IN_PROGRESS);
        SubTask subTask2 = new SubTask("Подзадача3", "Описание 3 подзадачи", Status.DONE);
        taskManager.addSubtask(subTask, 0);
        taskManager.addSubtask(subTask1, 0);
        taskManager.addSubtask(subTask2, 0);
        SubTask subTask5 = new SubTask("Подзадача", "Новая подзадача", 2, Status.DONE, epic);
        taskManager.updateSubtask(subTask5);
        assertEquals(Status.DONE, taskManager.getEpic(0).getStatus());
    }

    @DisplayName("Проверка статуса созданого эпика c подзадачами, при удалении подзадач")
    @Test
    void testEpicStatusRemoveSubtasksStatusEpicNew() {
        Epic epic = new Epic("Эпик1", "Описание первого эпика");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Подзадача3", "Описание 3 подзадачи", Status.DONE);
        SubTask subTask1 = new SubTask("Подзадача3", "Описание 3 подзадачи", Status.IN_PROGRESS);
        SubTask subTask2 = new SubTask("Подзадача3", "Описание 3 подзадачи", Status.DONE);
        taskManager.addSubtask(subTask, 0);
        taskManager.addSubtask(subTask1, 0);
        taskManager.addSubtask(subTask2, 0);
        taskManager.removeAllSubtask();
        assertEquals(Status.NEW, taskManager.getEpic(0).getStatus());
    }

    @DisplayName("Проверка метода getDefault")
    @Test
    void testinMemoryTaskManagerIsManagerDefault() {
        InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
        assertEquals(taskManager.getClass(), inMemoryTaskManager.getClass());
    }

    @DisplayName("Проверка удаления подзадач из эпика")
    @Test
    void testRemoveSubtaskFromEpic() {
        Epic epic = new Epic("Эпик1", "Описание первого эпика");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание", Status.NEW);
        SubTask subTask2 = new SubTask("Подзадача 2", "Описание", Status.NEW);
        taskManager.addSubtask(subTask1, 0);
        taskManager.addSubtask(subTask2, 0);
        taskManager.removeSubtasks(1);
        assertEquals(1, taskManager.getSubtasksEpic(0).size());
        assertFalse(taskManager.getSubtasksEpic(0).contains(subTask1));
        assertEquals(1, epic.getSubTasks().size());
    }

    @DisplayName("Проверка изменения статуса через сеттер")
    @Test
    void testUpdateTaskStatusUsingSetter() {
        Task task = new Task("Задача 1", "Описание задачи", Status.NEW);
        taskManager.addTask(task);
        task.setStatus(Status.IN_PROGRESS);
        assertEquals(Status.IN_PROGRESS, taskManager.getTask(0).getStatus());
        task.setStatus(Status.DONE);
        taskManager.updateTask(task);
        assertEquals(Status.DONE, taskManager.getTask(0).getStatus());
    }

    @DisplayName("Проверка измнения описания подзадачи эпика через сеттер")
    @Test
    void testUpdateSubtaskUsingSetter() {
        Epic epic = new Epic("Эпик1", "Описание первого эпика");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("Подзадача 1", "Описание", Status.NEW);
        taskManager.addSubtask(subTask1, 0);
        subTask1.setDescription("Новое описание");
        assertEquals("Новое описание", epic.getSubTasks().getFirst().getDescription());
        assertEquals("Новое описание", taskManager.getSubtasks().getFirst().getDescription());
        subTask1.setDescription("Описание");
        taskManager.updateSubtask(subTask1);
        assertEquals("Описание", epic.getSubTasks().getFirst().getDescription());
        assertEquals("Описание", taskManager.getSubtasks().getFirst().getDescription());
    }

    @DisplayName("Изменение статуса подзадачи без уведомления менеджера")
    @Test
    void testSubtaskStatusChangedWithoutManagerUpdate() {
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Подзадача", "Описание", Status.NEW);
        taskManager.addSubtask(subTask, epic.getId());
        subTask.setStatus(Status.DONE);
        assertNotEquals(Status.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }
}
