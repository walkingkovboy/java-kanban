package Service;

import Model.Epic;
import Model.Status;
import Model.SubTask;
import Model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TaskManager {
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private int seq = 0;

    private int generateId() {
        return seq++;
    }

    public TaskManager() {
        this.tasks = new HashMap<>();
    }

    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public SubTask createSubTask(SubTask subTask) {
        subTask.setId(generateId());
        subTask.getEpic().setSubTasks(subTask);
        subTasks.put(subTask.getId(), subTask);
        return subTask; // Не уверен на счет правоты, при иницилизации подзадачи она запрашивает эпик, в самом конструкторе идет привязка к эпику
    }

    public void updateEpic(Epic epic) { //Обновление эпика
        Epic saved = epics.get(epic.getId());
        epic.setStatus(saved.getStatus());
        for (SubTask subtask : saved.getSubTasks()) {
            epic.setSubTasks(subtask);
        }
        epics.put(epic.getId(), calculatingTheStatusEpic(epic));
    }

    public void updateSubTask(SubTask subTask) {
        subTask.setEpic(calculatingTheStatusEpic(subTask.getEpic()));
        subTasks.put(subTask.getId(), subTask);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public Task getTask(int id) {
        if (!check(tasks.get(id))) {
            return null;
        } else {
            return tasks.get(id);
        }
    }

    public SubTask getSubTask(int id) {
        if (!check(subTasks.get(id))) {
            return null;
        } else {
            return subTasks.get(id);
        }
    }

    public Epic getEpic(int id) {
        if (!check(epics.get(id))) {
            return null;
        } else {
            return epics.get(id);
        }
    }

    public void allGetTasks() {
        System.out.println(tasks.values());
    }

    public void allGetEpics() {
        System.out.println(epics.values());
    }

    public void allGetSubTasks() {
        System.out.println(subTasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
        ;
    }

    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            epics.put(epic.getId(), calculatingTheStatusEpic(epic));
        }
        subTasks.clear();
    }

    public void removeAllEpics() {
        for (SubTask subTask : subTasks.values()) {
            subTask.setEpic(null);
        }
        epics.clear(); //Перепутал хэщмапы похоже
    }

    public void removeTask(int id) {
        if (check(tasks.get(id))) {
            tasks.remove(id);
        }
    }

    public void removeSubTask(int id) {
        if (check(subTasks.get(id))) {
            epics.get(subTasks.get(id).getEpic().getId()).getSubTasks().remove(subTasks.get(id));
            epics.put(subTasks.get(id).getEpic().getId(), calculatingTheStatusEpic(epics.get(subTasks.get(id).getEpic().getId())));
            subTasks.remove(id);
        }
    }

    public void removeEpic(int id) {
        if (check(epics.get(id))) {
            for (SubTask subTask : epics.get(id).getSubTasks()) {
                subTask.setEpic(null);
            }
            epics.remove(id);
        }
    }

    public ArrayList<SubTask> getSubTaskEpic(Epic epic) {
        return (ArrayList<SubTask>) epic.getSubTasks();
    }

    private Epic calculatingTheStatusEpic(Epic epic) {
        if (epic.getSubTasks() == null) {
            epic.setStatus(Status.NEW);
            return epic;
        } else {
            int flag = epic.getSubTasks().size();
            int summaNEW = 0;
            int summaDONE = 0;
            for (Task task : epic.getSubTasks()) {
                if (task.getStatus() == Status.NEW) {
                    summaNEW++;
                } else if (task.getStatus() == Status.DONE) {
                    summaDONE++;
                }
            }
            if (flag == summaNEW) {
                epic.setStatus(Status.NEW);
                return epic;
            } else if (flag == summaDONE) {
                epic.setStatus(Status.DONE);
                return epic;
            } else {
                epic.setStatus(Status.IN_PROGRESS);
                return epic;
            }
        }
    }

    private boolean check(Object object) {
        if (object != null) {
            return true;
        } else {
            System.out.println("Айди не найден");
            return false;
        }
    }
}
