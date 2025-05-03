import com.google.gson.Gson;
import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskManagerTasksTest {

    TaskManager manager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(manager);
    Gson gson = HttpTaskServer.getGson();
    protected Epic epic;
    protected Task task;
    protected Task task1;
    protected Task task2;
    protected SubTask subTask;
    protected SubTask subTask1;
    protected SubTask subTask2;

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.removeAllTasks();
        manager.removeAllSubtask();
        manager.removeAllEpics();
        taskServer.start();
        epic = new Epic("Эпик1", "Описание первого эпика");
        task = new Task("Задача 1", "Описание задачи", Status.NEW);
        subTask = new SubTask("Подзадача 1", "Описание", Status.DONE);
        subTask1 = new SubTask("Подзадача2", "Описание 2 подзадачи", Status.IN_PROGRESS);
        subTask2 = new SubTask("Подзадача3", "Описание 3 подзадачи", Status.DONE);
        task1 = new Task("Пересекающаяся задача", "Описание задачи", Status.NEW);
        task2 = new Task("Непересекающаяся задача", "Описание задачи", Status.NEW);
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @DisplayName("Проверка на добавление задачи")
    @Test
    public void testAddTask() throws IOException, InterruptedException {
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ожидаем получить статус 201");
        List<Task> tasksFromManager = manager.getTasks();
        assertEquals(1, tasksFromManager.size(), "Задача не была добавлена");
        assertEquals("Задача 1", tasksFromManager.getFirst().getName(), "Неверное название");
    }

    @DisplayName("Проверка на получение задачи")
    @Test
    public void testGetTask() throws IOException, InterruptedException {
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?id=0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидаем статус код 200");
        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertNotNull(taskFromResponse, "Задача не найдена");
        assertEquals("Задача 1", taskFromResponse.getName(), "Неверное имя");
    }

    @DisplayName("Проверка удаления задачи")
    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?id=0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидаем статус код 200");
        List<Task> tasksFromManager = manager.getTasks();
        assertTrue(tasksFromManager.isEmpty(), "Задача не была удалена");
    }

    @DisplayName("Проверка обновления задачи")
    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        manager.addTask(task);
        task.setName("Обновленная задача");
        String taskJson = gson.toJson(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидаем статус 201 для обновления задачи");
        List<Task> tasksFromManager = manager.getTasks();
        assertEquals(1, tasksFromManager.size(), "Задача не была обновлена");
        assertEquals("Обновленная задача", tasksFromManager.getFirst().getName(), "Неверное название задачи");
    }

    @DisplayName("Проверка пересечения времени задач")
    @Test
    public void testTaskTimeIntersection() throws IOException, InterruptedException {
        task.setStartTime(LocalDateTime.parse("2025-05-01T10:00"));
        task.setDuration(Duration.ofMinutes(60));
        task1.setStartTime(LocalDateTime.parse("2025-05-01T10:30"));
        task1.setDuration(Duration.ofMinutes(30));
        String taskJson = gson.toJson(task);
        String task1Json = gson.toJson(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ожидаем статус 201 после добавления задачи");
        request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(task1Json)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, response.statusCode(), "Ожидаем статус 406 при пересечении задач");
        assertTrue(response.body().contains("Задача пересекается по времени с другой задачей"), "Сообщение об ошибке не совпадает");
    }

    @DisplayName("Проверка получения истории")
    @Test
    public void testHistory() throws IOException, InterruptedException {
        manager.addTask(task);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?id=0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидаем статус код 200");
        Task taskFromResponse = gson.fromJson(response.body(), Task.class);
        assertNotNull(taskFromResponse, "Задача не найдена");
        url = URI.create("http://localhost:8080/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидаем статус код 200");
        Task[] history = gson.fromJson(response.body(), Task[].class);
        assertTrue(history.length > 0, "История пуста");
        assertEquals("Задача 1", history[0].getName(), "Задача не найдена в истории");
    }

    @DisplayName("Проверка получения отсортированных задач")
    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        task.setStartTime(LocalDateTime.parse("01.05.2025 09:00", formatter));
        task.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.parse("01.05.2025 11:00", formatter));
        task1.setDuration(Duration.ofMinutes(30));
        manager.addTask(task);
        manager.addTask(task1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/priority");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидаем статус код 200");
        Task[] tasksFromResponse = gson.fromJson(response.body(), Task[].class);
        assertEquals(task.getName(), tasksFromResponse[0].getName(), "Первая задача должна быть Task 1");
        assertEquals(task1.getName(), tasksFromResponse[1].getName(), "Вторая задача должна быть Task 2");
    }

    @DisplayName("Проверка получения несуществующей задачи")
    @Test
    public void testGetNonExistentTask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks?id=999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Ожидаем статус код 404 для несуществующей задачи");
        assertTrue(response.body().contains("Задача с id=999 не найдена"), "Сообщение об ошибке не совпадает");
    }

    @DisplayName("Проверка добавления эпика")
    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        String epicJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ожидаем статус 201 для создания эпика");
        URI epicUrl = URI.create("http://localhost:8080/epics?id=0");
        request = HttpRequest.newBuilder().uri(epicUrl).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидаем статус 200 для получения эпика");
        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);
        assertNotNull(epicFromResponse, "Эпик не найден");
        assertNull(epicFromResponse.getStartTime(), "Start time должно быть null");
        assertNull(epicFromResponse.getEndTime(), "End time должно быть null");
        assertNull(epicFromResponse.getDuration(), "Duration должно быть null");
        assertEquals("Эпик1", epicFromResponse.getName(), "Неверное имя эпика");
        assertEquals("Описание первого эпика", epicFromResponse.getDescription(), "Неверное описание эпика");
    }

    @DisplayName("Проверка добавления подзадачи к несуществующему эпику")
    @Test
    public void testAddSubtaskToNonExistentEpic() throws IOException, InterruptedException {
        subTask1.setEpicId(999);
        String subTaskJson = gson.toJson(subTask1);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(subTaskJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Ожидаем статус 404 для подзадачи, которая не может быть привязана к несуществующему эпику");
    }

    @DisplayName("Проверка удаления эпика")
    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        manager.addEpic(epic);
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics?id=" + epic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидаем статус 200 при удалении эпика");
        assertTrue(manager.getEpics().isEmpty(), "Эпик не был удален");
    }

    @DisplayName("Проверка добавления эпика с подзадачами")
    @Test
    public void testAddEpicWithSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        String epicJson = gson.toJson(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ожидаем статус 201 для создания эпика");
        int epicId = epic.getId();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        subTask1.setEpicId(epicId);
        subTask2.setEpicId(epicId);
        subTask1.setStartTime(LocalDateTime.parse("01.05.2025 09:00", formatter));
        subTask1.setDuration(Duration.ofMinutes(60));
        subTask2.setStartTime(LocalDateTime.parse("01.05.2025 11:00", formatter));
        subTask2.setDuration(Duration.ofMinutes(60));
        String subTaskJson1 = gson.toJson(subTask1);
        String subTaskJson2 = gson.toJson(subTask2);
        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks")).POST(HttpRequest.BodyPublishers.ofString(subTaskJson1)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ожидаем статус 201 для добавления подзадачи 1");
        request = HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks")).POST(HttpRequest.BodyPublishers.ofString(subTaskJson2)).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode(), "Ожидаем статус 201 для добавления подзадачи 2");
        URI epicUrl = URI.create("http://localhost:8080/epics?id=" + epicId);
        request = HttpRequest.newBuilder().uri(epicUrl).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидаем статус 200 для получения эпика");
        Epic epicFromResponse = gson.fromJson(response.body(), Epic.class);
        assertNotNull(epicFromResponse, "Эпик не найден");
        assertEquals(2, epicFromResponse.getSubTasks().size(), "Неверное количество подзадач у эпика");
        assertEquals(epicId, subTask1.getEpicId(), "Подзадача 1 не привязана к эпику");
        assertEquals(epicId, subTask2.getEpicId(), "Подзадача 2 не привязана к эпику");
        assertNotNull(epicFromResponse.getStartTime(), "У эпика не установлен стартовый момент");
        assertEquals(epicFromResponse.getStartTime(), subTask1.getStartTime(), "Время эпика совпадает со временем самой ранней подзадачи");
        assertNotNull(epicFromResponse.getEndTime(), "У эпика не установлен конечный момент");
        assertEquals(Status.IN_PROGRESS, epicFromResponse.getStatus(), "Статус эпика не обновился правильно");
        assertEquals(120, epicFromResponse.getDuration().toMinutes(), "Неверная продолжительность эпика");
        assertEquals("Эпик1", epicFromResponse.getName(), "Неверное имя эпика");
        assertEquals("Описание первого эпика", epicFromResponse.getDescription(), "Неверное описание эпика");
    }

    @DisplayName("Проверка удаления всех подзадач из эпика")
    @Test
    public void testDeleteAllSubtasksFromEpic() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest createEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic)))
                .build();
        assertEquals(201, client.send(createEpic, HttpResponse.BodyHandlers.ofString()).statusCode());
        int epicId = epic.getId();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        subTask1.setEpicId(epicId);
        subTask1.setStartTime(LocalDateTime.parse("01.05.2025 09:00", fmt));
        subTask1.setDuration(Duration.ofMinutes(60));
        subTask2.setEpicId(epicId);
        subTask2.setStartTime(LocalDateTime.parse("01.05.2025 11:00", fmt));
        subTask2.setDuration(Duration.ofMinutes(60));
        HttpRequest add1 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask1)))
                .build();
        HttpRequest add2 = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subTask2)))
                .build();
        assertEquals(201, client.send(add1, HttpResponse.BodyHandlers.ofString()).statusCode());
        assertEquals(201, client.send(add2, HttpResponse.BodyHandlers.ofString()).statusCode());
        HttpResponse<String> listResp = client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/subtasks")).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        SubTask[] allSubs = gson.fromJson(listResp.body(), SubTask[].class);
        for (SubTask st : allSubs) {
            HttpRequest del = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/subtasks?id=" + st.getId()))
                    .DELETE()
                    .build();
            assertEquals(200, client.send(del, HttpResponse.BodyHandlers.ofString()).statusCode());
        }
        HttpResponse<String> epicResp = client.send(
                HttpRequest.newBuilder().uri(URI.create("http://localhost:8080/epics?id=" + epicId)).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        Epic e = gson.fromJson(epicResp.body(), Epic.class);
        assertTrue(e.getSubTasks().isEmpty(), "Подзадач у эпика не должно быть");
        assertNull(e.getStartTime(), "StartTime должно быть null");
        assertNull(e.getEndTime(), "EndTime должно быть null");
        assertEquals(Duration.ZERO, e.getDuration(), "После удаления всех подзадач Duration эпика = 0");
        assertEquals(Status.NEW, e.getStatus(), "Статус эпика должен вернуться в NEW");
    }
}
