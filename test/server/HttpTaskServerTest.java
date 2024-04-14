package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.Test;
import server.enums.HttpCode;
import service.taskmanagers.Manager;
import service.taskmanagers.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskServerTest {

    static Gson gson;
    static HttpClient client;
    protected Task task1;
    protected Task task2;
    protected SubTask subTask1;
    protected SubTask subTask2;
    protected SubTask subTask3;
    protected Epic epic1;
    protected Epic epic2;
    TaskManager taskManager;
    KVServer kvServer;
    HttpTaskServer httpTaskServer;


    void setUp() throws IOException, InterruptedException {
        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        gson = new Gson();
        kvServer = new KVServer();
        kvServer.start();
        httpTaskServer = new HttpTaskServer();
        taskManager = Manager.getDefaultHttpManager();
        httpTaskServer.startTaskServer(taskManager, gson);
        task1 = new Task("Обычная задача1", "Первая", Status.NEW);
        task2 = new Task("Обычная задача2", "Вторая", Status.NEW);
        subTask1 = new SubTask("Подзадача1", "Первая подзадача", Status.NEW);
        subTask2 = new SubTask("Подзадача2", "Вторая подзадача", Status.NEW);
        subTask3 = new SubTask("Подзадача3", "Третья подзадача", Status.NEW);
        epic1 = new Epic("Эпик1", "Первый эпик");
        epic2 = new Epic("Эпик2", "Второй эпик");
    }

    void afterEach() {
        HttpTaskServer.stop();
        kvServer.stop();
    }


    @Test
    public void GETHistory() throws IOException, InterruptedException {
        setUp();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        //история пустая, должен вернуться пустой массив
        assertTrue(jsonElement.isJsonArray());
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        assertEquals(0, jsonElement.getAsJsonArray().size());
        taskManager.createTask(task1);
        taskManager.getTask(task1.getId());
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.isJsonArray());
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        assertEquals(1, jsonElement.getAsJsonArray().size());
        afterEach();
    }


    @Test
    public void GETTasks() throws IOException, InterruptedException {
        setUp();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        assertEquals(0, jsonElement.getAsJsonArray().size());
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        List<Task> testList = List.of(task1, task2);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        jsonElement = JsonParser.parseString(response.body());
        var tasksFromJson = gson.fromJson(jsonElement, new TypeToken<List<Task>>() {
        }.getType());
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        assertEquals(tasksFromJson, testList);
        afterEach();
    }

    @Test
    public void GETTaskById() throws IOException, InterruptedException {
        setUp();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/task/?id=0");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpCode.BAD_REQUEST.getCode(), response.statusCode());
        taskManager.createTask(task1);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        var taskFromJson = gson.fromJson(jsonElement.getAsJsonObject().get("value"), Task.class);
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        assertEquals(task1, taskFromJson);
        afterEach();
    }

    @Test
    public void POSTTask() throws IOException, InterruptedException {
        setUp();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/task/");
        String taskToJson = gson.toJson(task1);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(taskToJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpCode.CREATED.getCode(), response.statusCode());
        uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/task/?id=0");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        var taskFromJson = gson.fromJson(jsonElement.getAsJsonObject().get("value"), Task.class);
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        assertEquals(task1, taskFromJson);
        afterEach();
    }

    @Test
    public void DELETEAllTasks() throws IOException, InterruptedException {
        setUp();
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/task");
        //отправляем запрос на удаление
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        //отправляем запрос на получение, чтобы убедиться, что вернется пустой список
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.getAsJsonArray().isEmpty());
        afterEach();
    }

    @Test
    public void DELETETaskById() throws IOException, InterruptedException {
        setUp();
        taskManager.createTask(task1);
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/task/?id=1");
        //отправляем запрос на удаление
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        //отправляем запрос на получение, чтобы убедиться, что вернется пустой список
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpCode.BAD_REQUEST.getCode(), response.statusCode());
        afterEach();
    }

    @Test
    public void GETSubtasks() throws IOException, InterruptedException {
        setUp();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        assertEquals(0, jsonElement.getAsJsonArray().size());
        taskManager.createEpic(epic1);
        taskManager.addSubTask(subTask1, 1);
        taskManager.addSubTask(subTask2, 1);
        List<Task> testList = List.of(subTask1, subTask2);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        jsonElement = JsonParser.parseString(response.body());
        var tasksFromJson = gson.fromJson(jsonElement, new TypeToken<List<SubTask>>() {
        }.getType());
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        assertEquals(tasksFromJson, testList);
        afterEach();
    }

    @Test
    public void DELETEAllSubtasks() throws IOException, InterruptedException {
        setUp();
        taskManager.createEpic(epic1);
        taskManager.addSubTask(subTask1, 1);
        taskManager.addSubTask(subTask2, 1);
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/subtask");
        //отправляем запрос на удаление
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        //отправляем запрос на получение, чтобы убедиться, что вернется пустой список
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.getAsJsonArray().isEmpty());
        afterEach();
    }

    @Test
    public void DELETESubtaskById() throws IOException, InterruptedException {
        setUp();
        taskManager.createEpic(epic1);
        taskManager.addSubTask(subTask1, 1);
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/subtask/?id=1");
        //отправляем запрос на удаление
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        //отправляем запрос на получение, чтобы убедиться, что вернется пустой список
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        afterEach();
    }

    @Test
    public void GETEpics() throws IOException, InterruptedException {
        setUp();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        assertEquals(0, jsonElement.getAsJsonArray().size());
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        List<Epic> testList = List.of(epic1, epic2);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        jsonElement = JsonParser.parseString(response.body());
        var epicsFromJson = gson.fromJson(jsonElement, new TypeToken<List<Epic>>() {
        }.getType());
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        assertEquals(testList, epicsFromJson);
        afterEach();
    }

    @Test
    public void GETEpicByID() throws IOException, InterruptedException {
        setUp();
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/epic/?id=0");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpCode.BAD_REQUEST.getCode(), response.statusCode());
        taskManager.createEpic(epic1);
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        var epicFromJson = gson.fromJson(jsonElement.getAsJsonObject().get("value"), Epic.class);
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        assertEquals(epic1, epicFromJson);
        afterEach();
    }

    @Test
    public void POSTEpic() throws IOException, InterruptedException {
        setUp();
        taskManager.createEpic(epic1);
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/epic");
        String epicToJson = gson.toJson(epic1);
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString(epicToJson)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpCode.CREATED.getCode(), response.statusCode());
        uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/epic/?id=0");
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        var epicFromJson = gson.fromJson(jsonElement.getAsJsonObject().get("value"), Epic.class);
        assertEquals(HttpCode.SUCCESS.getCode(), response.statusCode());
        assertEquals(epic1, epicFromJson);
        afterEach();
    }

    @Test
    public void DELETEEpics() throws IOException, InterruptedException {
        setUp();
        taskManager.createEpic(epic1);
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/epic");
        //отправляем запрос на удаление
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        //отправляем запрос на получение, чтобы убедиться, что вернется пустой список
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonElement jsonElement = JsonParser.parseString(response.body());
        assertTrue(jsonElement.getAsJsonArray().isEmpty());
        afterEach();
    }

    @Test
    public void DELETEEpicById() throws IOException, InterruptedException {
        setUp();
        taskManager.createEpic(epic1);
        URI uri = URI.create("http://localhost:" + HttpTaskServer.getPort() + "/tasks/epic/?id=0");
        //отправляем запрос на удаление
        HttpRequest request = HttpRequest.newBuilder().uri(uri).DELETE().build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        //отправляем запрос на получение, чтобы убедиться, что вернется пустой список
        request = HttpRequest.newBuilder().uri(uri).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(HttpCode.BAD_REQUEST.getCode(), response.statusCode());
        afterEach();
    }
}
