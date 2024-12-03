package Manager;

import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int identifier = 0;
    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();

    public void addTask(Task task) {
        tasks.put(identifier, task);
        task.setId(identifier);
        identifier++;
        System.out.println("Задача добавлена в память!");
    }

    public void addEpic(Epic epic) {
        epic.setId(identifier);
        epics.put(identifier, epic);
        identifier++;
        System.out.println("Эпик добавлен в память!");
    }

    public void addSubtask(SubTask subTask, int idEpic) {
        subtasks.put(identifier, subTask);
        subTask.setId(identifier);
        epics.get(idEpic).setSubTasks(subTask);
        subTask.setEpic(epics.get(idEpic));
        checkStatusEpic(epics.get(idEpic));
        identifier++;
        System.out.println("Подзадача добавлена в память!");
    }

    public void updateTask(Task task) {
        if (checkTask(task)) {
            tasks.put(task.getId(), task);
            System.out.println("Информация о задаче обновлена!");
        }
    }

    public void updateEpic(Epic epic) {
        if (checkEpic(epic)) {
            epics.put(epic.getId(), epic);
            System.out.println("Информация об Эпике обновлена");
        }
    }

    public void updateSubtask(SubTask subTask) {
        if (checkSubtask(subTask)) {
            epics.get(subtasks.get(subTask.getId()).getEpic().getId()).setSubTasks(subTask);
            subtasks.put(subTask.getId(), subTask);
            checkStatusEpic(epics.get(subTask.getEpic().getId()));
            System.out.println("Информация о подзадаче обновленна, статус эпик пересмотрен");
        }
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<SubTask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void removeAllTasks() {
        tasks.clear();
        System.out.println("Все задачи удалены");
    }

    public void removeAllEpics() {
        epics.clear();
        if (!subtasks.isEmpty()) {
            for (SubTask subtask : subtasks.values()) {
                subtask.setEpic(null);
            }
        }
        System.out.println("Все Эпики удалены, у текущих(сохраненных) подзадач больше нет Эпиков");
    }

    public void removeAllSubtask() {
        subtasks.clear();
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                epic.removeAllSubtasks();
                checkStatusEpic(epic);
            }
        }
        System.out.println("Все подзадачи удалены,статус у текущих Эпиков обновлен");
    }

    public void removeTask(int id) {
        if (checkTask(tasks.get(id))) {
            tasks.remove(id);
            System.out.println("Задача с айди " + id + " удалена");
        }
    }

    public void removeEpic(int id) {
        if (checkEpic(epics.get(id))) {

            if (!subtasks.isEmpty()) {
                for (SubTask subTask : subtasks.values()) {
                    if (subTask.getEpic().getId() == id) {
                        subTask.setEpic(null);
                    }
                }
            }
            epics.remove(id);
            System.out.println("Эпик с айди " + id + " удален. Теперь у текущих(сохраненных) подзадач нет эпика");
        }
    }

    public void removeSubtasks(int id) {
        if (checkSubtask(subtasks.get(id))) {
            for (Epic epic : epics.values()) {
                if (epic.getId() == id) {
                    epic.removeSubtask(subtasks.get(id));
                    checkStatusEpic(epic);
                }
            }
            subtasks.remove(id);
            System.out.println("Задача с айди " + id + " удалена. Статус эпиков обновлен.");
        }
    }

    public Task getTask(int id) {
        if (checkTask(tasks.get(id))) {
            return tasks.get(id);
        }
        System.out.println();
        return null;
    }

    public Epic getEpic(int id) {
        if (checkEpic(epics.get(id))) {
            return epics.get(id);
        }
        System.out.println();
        return null;
    }

    public SubTask getSubtask(int id) {
        if (checkSubtask(subtasks.get(id))) {
            return subtasks.get(id);
        }
        System.out.println();
        return null;
    }

    public ArrayList<SubTask> getSubtasksEpic(int id) {
        if (checkEpic(epics.get(id))) {
            return new ArrayList<>(epics.get(id).getSubTasks());
        }
        System.out.println();
        return null;
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
        int countStatusNew = 0;
        int countStatusDone = 0;
        for (SubTask stask : epic.getSubTasks()) {
            if (stask.getStatus() == Status.NEW) {
                countStatusNew++;
            } else if (stask.getStatus() == Status.DONE) {
                countStatusDone++;
            }
        }
        if (countStatusNew == epic.getSubTasks().size()) {
            epic.setStatus(Status.NEW);
        } else if (countStatusDone == epic.getSubTasks().size()) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);

        }
    }
}
