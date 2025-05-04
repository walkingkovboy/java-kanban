package test;

import com.google.gson.Gson;
import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class HttpTaskManagerTasksTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    private enum Method {GET, POST, PUT, DELETE}

    @BeforeAll
    void initClientAndGson() {
        client = HttpClient.newHttpClient();
        gson = HttpTaskServer.getGson();
    }

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    private HttpResponse<String> request(Method method, String uri, String body) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(uri));
        switch (method) {
            case GET:
                builder.GET();
                break;
            case POST:
                builder.POST(HttpRequest.BodyPublishers.ofString(body));
                break;
            case PUT:
                builder.PUT(HttpRequest.BodyPublishers.ofString(body));
                break;
            case DELETE:
                builder.DELETE();
                break;
            default:
                throw new IllegalArgumentException("Не поддерживаемый метод: " + method);
        }
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    @Nested
    @DisplayName("Тесты операций с задачами")
    class TaskTests {
        private Task task;

        @BeforeEach
        void createTask() {
            task = new Task("Задача 1", "Описание задачи", Status.NEW);
        }

        @Test
        @DisplayName("Добавление задачи")
        void testAddTask() throws Exception {
            HttpResponse<String> resp = request(Method.POST, "http://localhost:8080/tasks", gson.toJson(task));
            assertEquals(201, resp.statusCode(), "Ожидаем статус 201 при создании задачи");
            assertEquals(1, manager.getTasks().size(), "Задача не добавилась в менеджер");
        }

        @Test
        @DisplayName("Получение задачи по id")
        void testGetTask() throws Exception {
            manager.addTask(task);
            HttpResponse<String> resp = request(Method.GET, "http://localhost:8080/tasks?id=0", null);
            assertEquals(200, resp.statusCode(), "Ожидаем статус 200 при получении задачи");
            Task actual = gson.fromJson(resp.body(), Task.class);
            assertEquals("Задача 1", actual.getName(), "Неверное имя задачи");
            assertEquals(Status.NEW, actual.getStatus(), "Неверный статус задачи");
        }

        @Test
        @DisplayName("Обновление задачи")
        void testUpdateTask() throws Exception {
            manager.addTask(task);
            task.setName("Ненавижу тесты");
            HttpResponse<String> resp = request(Method.PUT, "http://localhost:8080/tasks", gson.toJson(task));
            assertEquals(200, resp.statusCode(), "Ожидаем статус 200 при обновлении");
            Task updated = manager.getTasks().getFirst();
            assertEquals("Ненавижу тесты", updated.getName(), "Имя задачи не обновилось");
            assertEquals(Status.NEW, updated.getStatus(), "Статус задачи изменился некорректно");
        }

        @Test
        @DisplayName("Удаление задачи по id")
        void testDeleteTask() throws Exception {
            manager.addTask(task);
            HttpResponse<String> resp = request(Method.DELETE, "http://localhost:8080/tasks?id=0", null);
            assertEquals(200, resp.statusCode(), "Ожидаем статус 200 при удалении");
            assertTrue(manager.getTasks().isEmpty(), "Задача не удалена");
        }

        @Test
        @DisplayName("Удаление несуществующей задачи возвращает 404")
        void testDeleteNonexistentTask() throws Exception {
            HttpResponse<String> resp = request(Method.DELETE, "http://localhost:8080/tasks?id=999", null);
            assertEquals(404, resp.statusCode(), "Ожидаем статус 404 для несуществующей задачи");
        }

        @Test
        @DisplayName("Удаление без id возвращает 400")
        void testDeleteWithoutId() throws Exception {
            HttpResponse<String> resp = request(Method.DELETE, "http://localhost:8080/tasks", null);
            assertEquals(400, resp.statusCode(), "Ожидаем статус 400 при отсутствии id");
        }

        @Test
        @DisplayName("Некорректный JSON при POST возвращает 500")
        void testInvalidJsonPost() throws Exception {
            HttpResponse<String> resp = request(Method.POST, "http://localhost:8080/tasks", "{Тут должно быть тело но его нет}");
            assertEquals(500, resp.statusCode(), "Ожидаем статус 500");
        }

        @Test
        @DisplayName("Некорректный JSON при PUT возвращает 500")
        void testInvalidJsonPut() throws Exception {
            HttpResponse<String> resp = request(Method.PUT, "http://localhost:8080/tasks", "{Тут должно быть тело но его нет}");
            assertEquals(500, resp.statusCode(), "Ожидаем статус 500");
        }
    }

    @Nested
    @DisplayName("Тесты пересечения времени задач")
    class TaskIntersectionTests {
        private Task task1, task2;

        @BeforeEach
        void initTasks() {
            task1 = new Task("Задача 1", "Вынести мусор", Status.NEW);
            task2 = new Task("Задача 2", "Помыть посуду", Status.NEW);
        }

        @Test
        @DisplayName("Пересечение по времени возвращает 406")
        void testTimeIntersection() throws Exception {
            task1.setStartTime(LocalDateTime.parse("2025-05-01T10:00"));
            task1.setDuration(Duration.ofMinutes(60));
            task2.setStartTime(LocalDateTime.parse("2025-05-01T10:30"));
            task2.setDuration(Duration.ofMinutes(30));
            assertEquals(201, request(Method.POST, "http://localhost:8080/tasks", gson.toJson(task1)).statusCode());
            HttpResponse<String> resp = request(Method.POST, "http://localhost:8080/tasks", gson.toJson(task2));
            assertEquals(406, resp.statusCode(), "Ожидаем статус 406 при пересечении задач");
        }
    }

    @Nested
    @DisplayName("Тесты истории запросов")
    class HistoryTests {
        @Test
        @DisplayName("Запись истории GET-запросов")
        void testHistory() throws Exception {
            Task task1 = new Task("Задача 1", "Первый", Status.NEW);
            Task task2 = new Task("Задача 2", "Второй", Status.NEW);
            manager.addTask(task1);
            manager.addTask(task2);
            request(Method.GET, "http://localhost:8080/tasks?id=0", null);
            request(Method.GET, "http://localhost:8080/tasks?id=1", null);
            HttpResponse<String> resp = request(Method.GET, "http://localhost:8080/history", null);
            assertEquals(200, resp.statusCode(), "Ожидаем 200 при запросе истории");
            Task[] hist = gson.fromJson(resp.body(), Task[].class);
            assertEquals(2, hist.length, "Ожидаем 2 записи в истории");
            assertEquals("Задача 1", hist[0].getName(), "Первая запись должна быть 'Задача 1'");
            assertEquals("Задача 2", hist[1].getName(), "Вторая запись должна быть 'Задача 2'");
        }
    }

    @Nested
    @DisplayName("Тесты приоритетных задач")
    class PriorityTests {
        @Test
        @DisplayName("Получение отсортированных задач")
        void testGetPrioritizedTasks() throws Exception {
            Task a = new Task("A", "d", Status.NEW);
            Task b = new Task("B", "d", Status.NEW);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            a.setStartTime(LocalDateTime.parse("01.05.2025 09:00", fmt));
            a.setDuration(Duration.ofMinutes(30));
            b.setStartTime(LocalDateTime.parse("01.05.2025 11:00", fmt));
            b.setDuration(Duration.ofMinutes(30));
            manager.addTask(a);
            manager.addTask(b);
            HttpResponse<String> resp = request(Method.GET, "http://localhost:8080/prioritized", null);
            assertEquals(200, resp.statusCode());
            Task[] arr = gson.fromJson(resp.body(), Task[].class);
            assertEquals("A", arr[0].getName());
            assertEquals("B", arr[1].getName());
        }
    }

    @Nested
    @DisplayName("Тесты Epic и SubTask")
    class EpicTests {
        private Epic epic;
        private SubTask sub;

        @BeforeEach
        void initEntities() {
            epic = new Epic("Эпик1", "Опиcание");
            sub = new SubTask("Подзадача1", "Описание", Status.NEW);
        }

        @Test
        @DisplayName("Добавление эпика")
        void testAddEpic() throws Exception {
            HttpResponse<String> resp = request(Method.POST, "http://localhost:8080/epics", gson.toJson(epic));
            assertEquals(201, resp.statusCode());
            Epic got = gson.fromJson(request(Method.GET, "http://localhost:8080/epics?id=0", null).body(), Epic.class);
            assertEquals("Эпик1", got.getName());
        }

        @Test
        @DisplayName("Добавление подзадачи к несуществующему эпику")
        void testAddSubToNonexistentEpic() throws Exception {
            sub.setEpicId(999);
            HttpResponse<String> resp = request(Method.POST, "http://localhost:8080/subtasks", gson.toJson(sub));
            assertEquals(404, resp.statusCode());
        }

        @Test
        @DisplayName("Ошибка при добавлении подзадачи с некорректными полями")
        void testInvalidSubtaskData() throws Exception {
            request(Method.POST, "http://localhost:8080/epics", gson.toJson(epic));
            SubTask brokenSub = new SubTask(null, null, null);
            brokenSub.setEpicId(0);
            HttpResponse<String> resp = request(Method.POST, "http://localhost:8080/subtasks", gson.toJson(brokenSub));
            assertEquals(406, resp.statusCode(), "Ожидаем статус 406 при валидационной ошибке");
        }

        @Test
        @DisplayName("Удаление эпика")
        void testDeleteEpic() throws Exception {
            manager.addEpic(epic);
            HttpResponse<String> resp = request(Method.DELETE, "http://localhost:8080/epics?id=0", null);
            assertEquals(200, resp.statusCode());
            assertTrue(manager.getEpics().isEmpty());
        }

        @Test
        @DisplayName("Добавление эпика с подзадачами и проверка полей")
        void testAddEpicWithSubtasks() throws Exception {
            request(Method.POST, "http://localhost:8080/epics", gson.toJson(epic));
            sub.setEpicId(0);
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            sub.setStartTime(LocalDateTime.parse("01.05.2025 09:00", fmt));
            sub.setDuration(Duration.ofMinutes(60));
            request(Method.POST, "http://localhost:8080/subtasks", gson.toJson(sub));
            HttpResponse<String> resp = request(Method.GET, "http://localhost:8080/epics?id=0", null);
            Epic got = gson.fromJson(resp.body(), Epic.class);
            assertEquals(1, got.getSubTasks().size(), "У эпика должна быть одна подзадача");
            assertNotNull(got.getStartTime(), "У эпика должен быть установлен startTime");
            assertNotNull(got.getEndTime(), "У эпика должен быть установлен endTime");
            assertEquals(Duration.ofMinutes(60), got.getDuration(), "Продолжительность эпика должна быть 60 минут");
            assertEquals(Status.NEW, got.getStatus(), "Статус эпика должен быть NEW");
        }

        @Test
        @DisplayName("Удаление всех подзадач из эпика")
        void testDeleteAllSubtasks() throws Exception {
            request(Method.POST, "http://localhost:8080/epics", gson.toJson(epic));
            sub.setEpicId(0);
            sub.setStartTime(LocalDateTime.now());
            sub.setDuration(Duration.ofMinutes(10));
            request(Method.POST, "http://localhost:8080/subtasks", gson.toJson(sub));
            SubTask[] subs = gson.fromJson(
                    request(Method.GET, "http://localhost:8080/subtasks", null).body(),
                    SubTask[].class
            );
            for (SubTask s : subs) {
                request(Method.DELETE, "http://localhost:8080/subtasks?id=" + s.getId(), null);
            }
            Epic got = gson.fromJson(
                    request(Method.GET, "http://localhost:8080/epics?id=0", null).body(),
                    Epic.class
            );
            assertTrue(got.getSubTasks().isEmpty(), "У эпика не должно остаться подзадач");
            assertNull(got.getStartTime(), "startTime должен быть null");
            assertNull(got.getEndTime(), "endTime должен быть null");
            assertEquals(Duration.ZERO, got.getDuration(), "Продолжительность должна быть 0");
            assertEquals(Status.NEW, got.getStatus(), "Статус эпика должен быть NEW");
        }
    }
}
