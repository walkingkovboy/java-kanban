package service.taskmanagers;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask, int idEpic);

    boolean updateEpic(Epic epic);

    boolean updateSubTask(SubTask subTask);

    boolean updateTask(Task task);

    Task getTask(int id);

    SubTask getSubTask(int id);

    Epic getEpic(int id);

    Collection<Task> getTasksAll();

    Collection<Epic> getEpicsAll();

    Collection<SubTask> getSubTasksAll();

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    boolean removeTask(int id);

    boolean removeSubTask(int id);

    boolean removeEpic(int id);

    ArrayList<SubTask> getSubTaskEpic(Epic epic);

    List<Task> getHistoryAll();

    List<Task> getPrioritizedTasks();
    boolean addSubTask(SubTask subTask,int epicId);
    boolean addTaskServer(Task task);
}

