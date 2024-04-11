package service.taskmanagers;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.history.HistoryManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private Path tasksFile;

    public FileBackedTaskManager(Path tasksFile) {
        super();
        this.tasksFile = tasksFile;
    }

    public static FileBackedTaskManager loadFromFile(Path path) {
        Integer maxId = 0;
        FileBackedTaskManager fm = new FileBackedTaskManager(path);
        try (BufferedReader bufferedReader = Files.newBufferedReader((path), StandardCharsets.UTF_8)) {
            String line = bufferedReader.readLine();
            while (!line.equals("")) {
                String[] values = line.split(",");
                Integer id = Integer.valueOf(values[1]);
                String title = values[2];
                Status status = Status.valueOf(values[3]);
                String description = values[4];
                switch (values[0]) {
                    case ("TASK"):
                        fm.addTask(new Task(title, description, status, id));
                        break;
                    case ("EPIC"):
                        fm.addEpic(new Epic(title, description, status, id));
                        break;
                    case ("SUBTASK"):
                        int epicId = Integer.valueOf(values[5]);
                        fm.addSubtask(new SubTask(title, description, status, id), epicId);
                        break;
                }
                line = bufferedReader.readLine();
            }
            String history = bufferedReader.readLine();
            String[] historyElems = history.split(",");
            for (String historyElem : historyElems) {
                Integer id = Integer.valueOf(historyElem);
                fm.getTask(id);
                fm.getEpic(id);
                fm.getSubTask(id);
            }
            fm.setLastId(maxId);
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
        return fm;
    }

    private static String historyToString(HistoryManager manager) {
        return manager.getHistory().stream()
                .map(Task::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> ids = new ArrayList<>();
        String[] values = value.split(",");
        for (String id : values) {
            ids.add(Integer.valueOf(id));
        }
        return ids;
    }

    @Override
    public Task createTask(Task task) {
        Task res = super.createTask(task);
        save();
        return res;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic res = super.createEpic(epic);
        save();
        return res;
    }

    @Override
    public SubTask createSubTask(SubTask subTask, int idEpic) {
        SubTask res = super.createSubTask(subTask, idEpic);
        save();
        return res;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Task getTask(int id) {
        Task res = super.getTask(id);
        save();
        return res;
    }

    @Override
    public SubTask getSubTask(int id) {
        SubTask res = super.getSubTask(id);
        save();
        return res;
    }

    @Override
    public Epic getEpic(int id) {
        Epic res = super.getEpic(id);
        save();
        return res;
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeSubTask(int id) {
        super.removeSubTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public ArrayList<SubTask> getSubTaskEpic(Epic epic) {
        ArrayList<SubTask> res = super.getSubTaskEpic(epic);
        save();
        return res;
    }

    @Override
    public List<Task> getHistoryAll() {
        List<Task> res = super.getHistoryAll();
        save();
        return res;
    }

    public void save() {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(tasksFile, StandardCharsets.UTF_8)) {
            for (Task task : super.getTasksAll()) {
                bufferedWriter.write(task.toString());
                bufferedWriter.write("\n");
            }
            for (Epic epic : super.getEpicsAll()) {
                bufferedWriter.write(epic.toString());
                bufferedWriter.write("\n");
            }
            for (SubTask subTask : super.getSubTasksAll()) {
                bufferedWriter.write(subTask.toString());
                bufferedWriter.write("\n");
            }
            bufferedWriter.write("\n");
            bufferedWriter.write(this.toStringHistory());
        } catch (IOException e) {
            throw new ManagerSaveException(e);
        }
    }

}
