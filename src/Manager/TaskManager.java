package Manager;

import entities.Epic;
import entities.SubTask;
import entities.Task;

import java.util.ArrayList;

public interface TaskManager {
    void addTask(Task task);

    void addEpic(Epic epic);

    void addSubtask(SubTask subTask, int idEpic);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(SubTask subTask);

    ArrayList<Task> getTasks();

    ArrayList<Epic> getEpics();

    ArrayList<SubTask> getSubtasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubtask();

    void removeTask(int id);

    void removeEpic(int id);

    void removeSubtasks(int id);

    Task getTask(int id);

    Epic getEpic(int id);

    SubTask getSubtask(int id);

    ArrayList<SubTask> getSubtasksEpic(int id);
}
