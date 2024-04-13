package service.taskmanagers;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.history.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

public class InMemoryTaskManager implements TaskManager {
    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = Manager.getDefaultHistory();
    }

    private HistoryManager historyManager;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;
    private int seq = 0;

    private int generateId() {
        return seq++;
    }

    protected void setLastId(int id) {
        this.seq = id;
    }

    protected void addTask(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    protected void addSubtask(SubTask subTask, int idEpic) {
        subTask.setEpic(epics.get(idEpic));
        epics.get(idEpic).setSubTasks(subTask);
        subTasks.put(subTask.getId(), subTask);
        subTask.setEpic(calculatingTheStatusEpic(subTask.getEpic()));
    }

    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), calculatingTheStatusEpic(epic));
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask, int idEpic) {
        subTask.setId(generateId());
        subTask.setEpic(epics.get(idEpic));
        epics.get(idEpic).setSubTasks(subTask);
        subTasks.put(subTask.getId(), subTask);
        subTask.setEpic(calculatingTheStatusEpic(subTask.getEpic()));
        return subTask;
    }

    @Override
    public void updateEpic(Epic epic) { //Обновление эпика
        Epic saved = epics.get(epic.getId());
        epic.setStatus(saved.getStatus());
        for (SubTask subtask : saved.getSubTasks()) {
            epic.setSubTasks(subtask);
        }
        epics.put(epic.getId(), calculatingTheStatusEpic(epic));
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        subTask.setEpic(calculatingTheStatusEpic(subTask.getEpic()));
        subTasks.put(subTask.getId(), subTask);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public Task getTask(int id) {
        if (!check(tasks.get(id))) {
            return null;
        } else {
            historyManager.add(tasks.get(id));
            return tasks.get(id);
        }
    }

    @Override
    public SubTask getSubTask(int id) {
        if (!check(subTasks.get(id))) {
            return null;
        } else {
            historyManager.add(subTasks.get(id));
            return subTasks.get(id);
        }
    }

    @Override
    public Epic getEpic(int id) {
        if (!check(epics.get(id))) {
            return null;
        } else {
            historyManager.add(epics.get(id));
            return epics.get(id);
        }
    }

    @Override
    public ArrayList<Task> getTasksAll() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicsAll() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasksAll() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
        ;
    }

    @Override
    public void removeAllSubTasks() {
        for (Epic epic : epics.values()) {
            epic.getSubTasks().clear();
            epics.put(epic.getId(), calculatingTheStatusEpic(epic));
        }
        subTasks.clear();
    }

    @Override
    public void removeAllEpics() {
        for (SubTask subTask : subTasks.values()) {
            subTask.setEpic(null);
        }
        epics.clear(); //Перепутал хэщмапы похоже
    }

    @Override
    public void removeTask(int id) {
        if (check(tasks.get(id))) {
            if (historyManager.getHistory().equals(tasks.get(id))) {
                historyManager.remove(tasks.get(id).getId());
            }
            tasks.remove(id);
        }
    }

    @Override
    public void removeSubTask(int id) {
        if (check(subTasks.get(id))) {
            epics.get(subTasks.get(id).getEpic().getId()).getSubTasks().remove(subTasks.get(id));
            epics.put(subTasks.get(id).getEpic().getId(), calculatingTheStatusEpic(epics.get(subTasks.get(id).getEpic().getId())));
            if (historyManager.getHistory().equals(subTasks.get(id))) {
                historyManager.remove(subTasks.get(id).getId());
            }
            subTasks.remove(id);
        }
    }

    @Override
    public void removeEpic(int id) {
        if (check(epics.get(id))) {
            for (SubTask subTask : epics.get(id).getSubTasks()) {
                subTask.setEpic(null);
            }
            if (historyManager.getHistory().equals(epics.get(id))) {
                historyManager.remove(epics.get(id).getId());
            }
            epics.remove(id);
        }
    }

    @Override
    public ArrayList<SubTask> getSubTaskEpic(Epic epic) {
        return epic.getSubTasks();
    }

    @Override
    public List<Task> getHistoryAll() {
        return historyManager.getHistory();
    }

    public String toStringHistory() {
        StringJoiner joiner = new StringJoiner(",");
        for (Task task : historyManager.getHistory()) {
            joiner.add(Integer.toString(task.getId()));
        }
        return joiner.toString();
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
        return object != null;
    }
}
