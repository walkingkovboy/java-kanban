package service.taskmanagers;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.history.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    private Set<Task> taskByTime = new TreeSet<>();
    private int seq = 0;

    private int generateId() {
        return seq++;
    }

    protected void setLastId(int id) {
        this.seq = id;
    }

    protected void addTask(Task task) {
        if (!isIntersectionWithTasks(task)) {
            tasks.put(task.getId(), task);
            taskByTime.add(task);
        }
    }

    protected void addEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    protected void addSubtask(SubTask subTask, int idEpic) {
        if (!isIntersectionWithTasks(subTask)) {
            subTask.setEpic(epics.get(idEpic));
            epics.get(idEpic).setSubTasks(subTask);
            subTasks.put(subTask.getId(), subTask);
            taskByTime.add(subTask);
            subTask.setEpic(calculatingTheStatusEpic(subTask.getEpic()));
        }
    }

    @Override
    public Task createTask(Task task) {
        if (!isIntersectionWithTasks(task)) {
            task.setId(generateId());
            tasks.put(task.getId(), task);
            taskByTime.add(task);
        }
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), calculatingTheStatusEpic(epic));
        epics.get(epic.getId()).recalculateStartTimeAndEndTime();
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask, int idEpic) {
        if (!isIntersectionWithTasks(subTask)) {
            subTask.setId(generateId());
            subTask.setEpic(epics.get(idEpic));
            epics.get(idEpic).setSubTasks(subTask);
            subTasks.put(subTask.getId(), subTask);
            taskByTime.add(subTask);
            subTask.setEpic(calculatingTheStatusEpic(subTask.getEpic()));
            epics.get(idEpic).recalculateStartTimeAndEndTime();
        }
        return subTask;
    }

    @Override
    public boolean addSubTask(SubTask subTask, int idEpic) {
        if (!isIntersectionWithTasks(subTask)) {
            subTask.setId(generateId());
            subTask.setEpic(epics.get(idEpic));
            if (epics.get(idEpic) != null) {
                epics.get(idEpic).setSubTasks(subTask);
                subTask.setEpic(calculatingTheStatusEpic(subTask.getEpic()));
                epics.get(idEpic).recalculateStartTimeAndEndTime();
            }
            subTasks.put(subTask.getId(), subTask);
            taskByTime.add(subTask);

            return true;
        }
        return false;
    }

    @Override
    public boolean addTaskServer(Task task) {
        if (!isIntersectionWithTasks(task)) {
            task.setId(generateId());
            tasks.put(task.getId(), task);
            taskByTime.add(task);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateEpic(Epic epic) { //Обновление эпика
        Epic saved = epics.get(epic.getId());
        epic.setStatus(saved.getStatus());
        saved.getSubTasks().stream()
                .peek(subtask -> epic.setSubTasks(subtask)).close();
        epics.put(epic.getId(), calculatingTheStatusEpic(epic));
        epics.get(epic.getId()).recalculateStartTimeAndEndTime();
        return true;
    }

    @Override
    public boolean updateSubTask(SubTask subTask) {
        if (!isIntersectionWithTasks(subTask)) {
            subTask.setEpic(calculatingTheStatusEpic(subTask.getEpic()));
            subTasks.put(subTask.getId(), subTask);
            epics.get(subTask.getEpic().getId()).recalculateStartTimeAndEndTime();
            return true;
        }
        return false;
    }

    @Override
    public boolean updateTask(Task task) {
        if (!isIntersectionWithTasks(task)) {
            tasks.put(task.getId(), task);
            return true;
        }
        return false;
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
    public Collection<Task> getTasksAll() {
        return tasks.values();
    }

    @Override
    public Collection<Epic> getEpicsAll() {
        return epics.values();
    }

    @Override
    public Collection<SubTask> getSubTasksAll() {
        return subTasks.values();
    }

    @Override
    public void removeAllTasks() {
        tasks.clear();
    }

    @Override
    public void removeAllSubTasks() {
        epics.values().stream().peek(epic -> epic.getSubTasks().clear())
                .peek(epic -> epics.put(epic.getId(), calculatingTheStatusEpic(epic)))
                .peek(epic -> epics.get(epic.getId()).recalculateStartTimeAndEndTime())
                .close();
        subTasks.clear();
    }

    @Override
    public void removeAllEpics() {
        subTasks.values().stream()
                .peek(subTask -> subTask.setEpic(null)).close();
        epics.clear(); //Перепутал хэщмапы похоже
    }

    @Override
    public boolean removeTask(int id) {
        if (check(tasks.get(id))) {
            if (historyManager.getHistory().equals(tasks.get(id))) {
                historyManager.remove(tasks.get(id).getId());
            }
            tasks.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeSubTask(int id) {
        if (check(subTasks.get(id))) {
            epics.get(subTasks.get(id).getEpic().getId()).getSubTasks().remove(subTasks.get(id));
            epics.put(subTasks.get(id).getEpic().getId(), calculatingTheStatusEpic(epics.get(subTasks.get(id).getEpic().getId())));
            if (historyManager.getHistory().equals(subTasks.get(id))) {
                historyManager.remove(subTasks.get(id).getId());
            }
            epics.get(subTasks.get(id).getEpic().getId()).recalculateStartTimeAndEndTime();
            subTasks.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeEpic(int id) {
        if (check(epics.get(id))) {
            for (SubTask subTask : epics.get(id).getSubTasks()) {
                subTask.setEpic(null);
            }
            if (historyManager.getHistory().equals(epics.get(id))) {
                historyManager.remove(epics.get(id).getId());
            }
            epics.remove(id);
            return true;
        }
        return false;
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
        return historyManager.getHistory().stream()
                .map(Task::getId)
                .map(String::valueOf)
                .collect(Collectors.joining(","));
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

    private boolean isIntersectionWithTasks(Task task) {
        if (!taskByTime.isEmpty()) {
            var bob = taskByTime.stream().filter(task2 -> task.getId() != task2.getId())
                    .map(task2 -> isTimeIntersection(
                            task.getStartTime().orElse(LocalDateTime.MIN),
                            task.getDuration(),
                            task2.getStartTime().orElse(LocalDateTime.MIN.plusNanos(1)),
                            task2.getDuration())).findFirst();
            if (bob.isPresent()) {
                return bob.get();
            }
        }
        return false;
    }

    private boolean isTimeIntersection(LocalDateTime startTime1, Duration duration1, LocalDateTime startTime2, Duration duration2) {
        LocalDateTime endTime1 = startTime1.plus(duration1);
        LocalDateTime endTime2 = startTime2.plus(duration2);
        return !(endTime1.isBefore(startTime2) || startTime1.isAfter(endTime2));
    }

    private boolean check(Object object) {
        return object != null;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(taskByTime);
    }
}
