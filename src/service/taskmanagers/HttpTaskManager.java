package service.taskmanagers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Epic;
import model.SubTask;
import model.Task;
import server.KVTaskClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTaskManager {

    private KVTaskClient client;
    private String serverURL;
    private static Gson gson = new Gson();
    private static final String TASK_KEY = "tasks";
    private static final String SUBTASK_KEY = "subtasks";
    private static final String EPIC_KEY = "epics";
    private static final String HISTORY_KEY = "history";
    private static final Type taskType = new TypeToken<List<Task>>() {}.getType();
    private static final Type subtaskType = new TypeToken<List<SubTask>>() {}.getType();
    private static final Type epicType = new TypeToken<List<Epic>>() {}.getType();
    private static final Type idType = new TypeToken<List<Integer>>() {}.getType();

    public HttpTaskManager(String serverURL) throws InterruptedException, IOException {
        super(null);
        this.serverURL = serverURL;
        client = new KVTaskClient(serverURL);
        loadFromServer();
    }

    @Override
    protected void save() {
        try {
            client.put(TASK_KEY, gson.toJson(super.getTasksAll().toArray()));
            client.put(EPIC_KEY, gson.toJson(super.getEpicsAll().toArray()));
            client.put(SUBTASK_KEY, gson.toJson(super.getSubTasksAll().toArray()));
            if (!super.getHistoryAll().isEmpty()) {
                client.put(HISTORY_KEY, gson.toJson(super.getHistoryAll().stream().map(Task::getId).collect(Collectors.toList())));
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Во время выполнения запроса произошла ошибка.", e);
        }
    }


    private void loadFromServer() throws IOException, InterruptedException {
        Optional<Collection<Task>> tasks = Optional.ofNullable(gson.fromJson(client.load(TASK_KEY), taskType));
        Optional<Collection<Epic>> epics = Optional.ofNullable(gson.fromJson(client.load(EPIC_KEY), epicType));
        Optional<Collection<SubTask>> subTasks = Optional.ofNullable(gson.fromJson(client.load(SUBTASK_KEY), subtaskType));
        Optional<Collection<Integer>> history = Optional.ofNullable(gson.fromJson(client.load(HISTORY_KEY), idType));
        if (tasks.isPresent()){
            for (Task task : tasks.get()) {
                super.addTask(task);
            }
        }
        if (epics.isPresent()){
            for (Epic epic : epics.get()) {
                super.addEpic(epic);
            }
        }
        if (subTasks.isPresent()){
            for (SubTask subTask : subTasks.get()){
                super.addSubTask(subTask,subTask.getEpic().getId());
            }
        }
        if(history.isPresent()){
            for(Integer id: history.get()){
                super.getTask(id);
                super.getEpic(id);
                super.getSubTask(id);
            }
        }
    }
}
