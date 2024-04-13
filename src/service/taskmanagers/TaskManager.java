package service.taskmanagers;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask, int idEpic);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void updateTask(Task task);

    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    ArrayList<Task> getTasksAll();

    ArrayList<Epic> getEpicsAll();

    ArrayList<SubTask> getSubTasksAll();

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpic(int id);

    ArrayList<SubTask> getSubTaskEpic(Epic epic);

    List<Task> getHistoryAll();
    Set<Task> getPrioritizedTasks();
}

