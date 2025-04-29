import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import manager.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    protected abstract T createTaskManager();

    @BeforeEach
    public void setup() {
        taskManager = createTaskManager();
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
        assertEquals(Status.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }

    @DisplayName("Проверка пустого Эпика")
    @Test
    public void epicStatusNewWhenNoSubTasks() {
        Epic epic = new Epic("Эпик", "Описание");
        assertEquals(Status.NEW, epic.getStatus());
    }

    @DisplayName("Проверка пересечения временных интервалов задач")
    @Test
    void testTaskTimeIntersection() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        Task task1 = new Task("Первая задача", "Описание задачи", Status.NEW);
        task1.setStartTime(LocalDateTime.parse("01.05.2025 10:00", formatter));
        task1.setDuration(Duration.ofMinutes(60));
        Task task2 = new Task("Пересекающаяся задача", "Описание задачи", Status.NEW);
        task2.setStartTime(LocalDateTime.parse("01.05.2025 10:30", formatter));
        task2.setDuration(Duration.ofMinutes(30));
        Task task3 = new Task("Непересекающаяся задача", "Описание задачи", Status.NEW);
        task3.setStartTime(LocalDateTime.parse("01.05.2025 11:30", formatter));
        task3.setDuration(Duration.ofMinutes(30));
        assertDoesNotThrow(() -> taskManager.addTask(task1));
        assertThrows(ValidationException.class, () -> taskManager.addTask(task2));
        assertDoesNotThrow(() -> taskManager.addTask(task3));
    }

    @DisplayName("Проверка вычесления окончания работы")
    @Test
    void testTaskEndTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        Task task1 = new Task("Первая задача", "Описание задачи", Status.NEW);
        task1.setStartTime(LocalDateTime.parse("01.05.2025 10:00", formatter));
        task1.setDuration(Duration.ofMinutes(60));
        LocalDateTime expectedEndTime = LocalDateTime.parse("01.05.2025 11:00", formatter);
        assertEquals(expectedEndTime, task1.getEndTime(), "Время окончания задачи рассчитано неверно");
    }

    @DisplayName("Проверка вычесления времени эпика относительно подзадач")
    @Test
    void testEpicTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        Epic epic = new Epic("Эпик", "Описание");
        taskManager.addEpic(epic);
        SubTask subTask1 = new SubTask("Первая задача", "Описание задачи", Status.NEW);
        subTask1.setStartTime(LocalDateTime.parse("01.05.2025 10:00", formatter));
        subTask1.setDuration(Duration.ofMinutes(60));
        taskManager.addSubtask(subTask1, epic.getId());
        SubTask subTask2 = new SubTask("Вторая подзадача", "Описание задачи", Status.NEW);
        subTask2.setStartTime(LocalDateTime.parse("01.05.2025 12:00", formatter));
        subTask2.setDuration(Duration.ofMinutes(60));
        taskManager.addSubtask(subTask2, epic.getId());
        SubTask subTask3 = new SubTask("Третья подзадача", "Описание задачи", Status.NEW);
        subTask3.setStartTime(LocalDateTime.parse("01.05.2025 09:00", formatter));
        subTask3.setDuration(Duration.ofMinutes(30));
        taskManager.addSubtask(subTask3, epic.getId());
        Epic updatedEpic = taskManager.getEpic(epic.getId());
        LocalDateTime expectedStartTime = LocalDateTime.parse("01.05.2025 09:00", formatter);
        LocalDateTime expectedEndTime = LocalDateTime.parse("01.05.2025 13:00", formatter);
        Duration expectedDuration = Duration.ofMinutes(150);
        assertEquals(expectedStartTime, updatedEpic.getStartTime(), "Неверное время старта эпика");
        assertEquals(expectedEndTime, updatedEpic.getEndTime(), "Неверное время окончания эпика");
        assertEquals(expectedDuration, updatedEpic.getDuration(), "Неверная продолжительность эпика");
    }

    @DisplayName("Проверка сортировки задач по времени старта в getPrioritizedTasks()")
    @Test
    void testPrioritizedTasksOrder() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        Task task1 = new Task("Задача 1", "Описание задачи 1", Status.NEW);
        task1.setStartTime(LocalDateTime.parse("01.05.2025 09:00", formatter));
        task1.setDuration(Duration.ofMinutes(30));
        Task task2 = new Task("Задача 2", "Описание задачи 2", Status.NEW);
        task2.setStartTime(LocalDateTime.parse("01.05.2025 11:00", formatter));
        task2.setDuration(Duration.ofMinutes(30));
        Epic epic = new Epic("Эпик", "Описание эпика");
        taskManager.addEpic(epic);
        SubTask subTask = new SubTask("Подзадача", "Описание подзадачи", Status.NEW);
        subTask.setStartTime(LocalDateTime.parse("01.05.2025 10:00", formatter));
        subTask.setDuration(Duration.ofMinutes(30));
        taskManager.addSubtask(subTask, epic.getId());
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(task1, prioritizedTasks.get(0), "Первая задача должна быть task1");
        assertEquals(epic, prioritizedTasks.get(1), "Вторая задача должна быть epic");
        assertEquals(subTask, prioritizedTasks.get(2), "Третья задача должна быть subTask");
        assertEquals(task2, prioritizedTasks.get(3), "Четвертая задача должна быть task2");
    }
}
