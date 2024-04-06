package Service.taskmanagers;

import Model.Epic;
import Model.SubTask;
import Model.Task;

import java.util.ArrayList;
import java.util.List;

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

    void getTasksAll();

    void getEpicsAll();

    void getSubTasksAll();

    void removeAllTasks();

    void removeAllSubTasks();

    void removeAllEpics();

    void removeTask(int id);

    void removeSubTask(int id);

    void removeEpic(int id);

    ArrayList<SubTask> getSubTaskEpic(Epic epic);

    List<Task> getHistoryAll();
}

