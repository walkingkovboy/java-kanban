package manager;

import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    private int identifier = 0;
    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, SubTask> subtasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(new TaskTimeComparator());

    @Override
    public void addTask(Task task) {
        validateTask(task);
        tasks.put(identifier, task);
        task.setId(identifier);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        identifier++;
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }


    @Override
    public void addEpic(Epic epic) {
        if (epic.getSubTasks() == null) {
            epic.setSubTasks(new ArrayList<>());
        }
        epic.setId(identifier);
        epics.put(identifier, epic);

        if (epic.getStartTime() != null) {
            prioritizedTasks.add(epic);
        }
        identifier++;
    }

    @Override
    public void addSubtask(SubTask subTask, int idEpic) {
        validateTask(subTask);
        subtasks.put(identifier, subTask);
        subTask.setId(identifier);
        epics.get(idEpic).setSubTasks(subTask);
        subTask.setEpicId(idEpic);
        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }
        checkStatusEpic(epics.get(idEpic));
        Epic epic = epics.get(idEpic);
        updateTimeEpic(epics.get(idEpic));
        prioritizedTasks.remove(epic);
        if (epic.getStartTime() != null) {
            updateTimeEpic(epics.get(idEpic));
            prioritizedTasks.add(epic);
        }
        identifier++;
    }

    @Override
    public void updateTask(Task task) {
        if (checkTask(task)) {
            validateTask(task);
            tasks.put(task.getId(), task);
            prioritizedTasks.remove(task);
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (checkEpic(epic)) {
            epics.put(epic.getId(), epic);
            prioritizedTasks.remove(epic);
            prioritizedTasks.add(epic);
        }
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        if (checkSubtask(subTask)) {
            validateTask(subTask);
            epics.get(subtasks.get(subTask.getId()).getEpicId()).setSubTasks(subTask);
            subtasks.put(subTask.getId(), subTask);
            checkStatusEpic(epics.get(subTask.getEpicId()));
            prioritizedTasks.remove(subTask);
            prioritizedTasks.add(subTask);
            updateTimeEpic(epics.get(subTask.getEpicId()));
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.values().forEach(prioritizedTasks::remove);
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.values().forEach(prioritizedTasks::remove);
        subtasks.values().forEach(prioritizedTasks::remove);
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void removeAllSubtask() {
        subtasks.values().forEach(prioritizedTasks::remove);
        subtasks.clear();
        if (!epics.isEmpty()) {
            epics.values().forEach(epic -> {
                epic.removeAllSubtasks();
                checkStatusEpic(epic);
                prioritizedTasks.remove(epic);
                updateTimeEpic(epic);
            });
        }
    }

    @Override
    public void removeTask(int id) {
        if (checkTask(tasks.get(id))) {
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeEpic(int id) {
        if (checkEpic(epics.get(id))) {

            if (!subtasks.isEmpty()) {
                subtasks.values().forEach(subTask -> {
                    if (subTask.getEpicId() == id) {
                        historyManager.remove(subTask.getId());
                        subTask.setEpicId(null);
                    }
                });
            }
            prioritizedTasks.remove(epics.get(id));
            epics.remove(id);
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubtasks(int id) {
        SubTask subTask = subtasks.get(id);
        if (checkSubtask(subTask)) {
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subTask);
                checkStatusEpic(epic);
                prioritizedTasks.remove(epic);
            }
            if (subTask.getStartTime() != null) {
                prioritizedTasks.remove(subTask);
            }
            subtasks.remove(id);
            historyManager.remove(id);
            if (epic != null) {
                if (epic.getStartTime() != null) {
                    updateTimeEpic(epics.get(subTask.getEpicId()));
                    prioritizedTasks.add(epic);
                }
            }
        }
    }

    @Override
    public Task getTask(int id) {
        if (tasks.containsKey(id)) {
            historyManager.addHistory(tasks.get(id));
        }
        return tasks.get(id);
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.containsKey(id)) {
            historyManager.addHistory(epics.get(id));
        }
        return epics.get(id);
    }

    @Override
    public SubTask getSubtask(int id) {
        if (subtasks.containsKey(id)) {
            historyManager.addHistory(subtasks.get(id));
        }
        return subtasks.get(id);
    }

    @Override
    public ArrayList<SubTask> getSubtasksEpic(int id) {
        if (checkEpic(epics.get(id))) {
            return new ArrayList<>(epics.get(id).getSubTasks());
        }
        return null;
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public HistoryManager getHistoryManager() {
        return historyManager;
    }

    private void updateTimeEpic(Epic epic) {
        epic.setStartTime(epic.getSubTasks().stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null));
        epic.setEndTime(epic.getSubTasks().stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null));
        epic.setDuration(epic.getSubTasks().stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus));
    }

    private boolean checkEpic(Epic epic) {
        if (epic == null) {
            System.out.println("Получена пустая ссылка! Укажите корректный айди");
            return false;
        }
        if (epics.isEmpty()) {
            System.out.println("Список эпиков пуст! Создайте сначало эпики");
            return false;
        } else if (!epics.containsKey(epic.getId())) {
            System.out.println("Такого эпика нет!");
            return false;
        }
        return true;
    }

    private boolean checkSubtask(SubTask subTask) {
        if (subTask == null) {
            System.out.println("Получена пустая ссылка!У подзадачи! Укажите корректный айди");
            return false;
        }
        if (subtasks.isEmpty()) {
            System.out.println("Список подзадач пуст! Создайте сначало подзадачи");
            return false;
        } else if (!subtasks.containsKey(subTask.getId())) {
            System.out.println("Такой подзадачи нет!");
            return false;
        }
        return true;
    }

    private boolean checkTask(Task task) {
        if (task == null) {
            System.out.println("Получена пустая ссылка!Укажите корректный айди");
            return false;
        }
        if (tasks.isEmpty()) {
            System.out.println("Список задач пуст! Добавьте сначало задачи");
            return false;
        } else if (!tasks.containsKey(task.getId())) {
            System.out.println("Такой задачи нет!");
            return false;
        }
        return true;
    }

    private void checkStatusEpic(Epic epic) {
        if (epic.getSubTasks().isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
        long countStatusNew = epic.getSubTasks().stream()
                .filter(subTask -> subTask.getStatus() == Status.NEW)
                .count();

        long countStatusDone = epic.getSubTasks().stream()
                .filter(subTask -> subTask.getStatus() == Status.DONE)
                .count();
        if (countStatusNew == epic.getSubTasks().size()) {
            epic.setStatus(Status.NEW);
        } else if (countStatusDone == epic.getSubTasks().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);

        }
    }

    private boolean checkIntersectionTasks(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        return !(task1.getEndTime().isBefore(task2.getStartTime()) || task1.getStartTime().isAfter(task2.getEndTime()));
    }

    private void validateTask(Task newTask) {
        boolean hasOverlap = prioritizedTasks.stream()
                .anyMatch(existingTask -> checkIntersectionTasks(newTask, existingTask));
        if (hasOverlap) {
            throw new ValidationException("Задача пересекается по времени с другой задачей");
        }
    }
}
