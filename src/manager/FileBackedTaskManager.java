package manager;

import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;

import java.io.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file = new File("tasks.csv");

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",");
        if (task.getClass().equals(Epic.class)) {
            sb.append("EPIC,");
        } else if (task.getClass().equals(SubTask.class)) {
            sb.append("SUBTASK,");
        } else {
            sb.append("TASK,");
        }
        sb.append(task.getName()).append(",");
        sb.append(task.getStatus()).append(",");
        sb.append(task.getDescription()).append(",");
        if (task.getClass().equals(SubTask.class)) {
            sb.append(((SubTask) task).getEpic().getId());
        }
        return sb.toString();
    }

    private Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        switch (type) {
            case "TASK":
                Task task = new Task(name, description, status);
                task.setId(id);
                task.setStatus(status);
                return task;
            case "EPIC":
                Epic epic = new Epic(name, description);
                epic.setId(id);
                epic.setStatus(status);
                return epic;
            case "SUBTASK":
                return new SubTask(name, description, id, status, Integer.parseInt(parts[5]));
            default:
                throw new ManagerLoadException("Неизвестный тип задачи при загрузке: " + type);
        }

    }

    @Override
    public void addTask(Task task) {
        super.addTask(task);
        save();
    }

    @Override
    public void addEpic(Epic epic) {
        super.addEpic(epic);
        save();
    }

    @Override
    public void addSubtask(SubTask subTask, int idEpic) {
        super.addSubtask(subTask, idEpic);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        super.updateSubtask(subTask);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubtask() {
        super.removeAllSubtask();
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public void removeSubtasks(int id) {
        super.removeSubtasks(id);
        save();
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");

            for (Task task : tasks.values()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : epics.values()) {
                writer.write(toString(epic) + "\n");
            }
            for (SubTask subTask : subtasks.values()) {
                writer.write(toString(subTask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            reader.readLine();
            int maxId = 0;
            while ((line = reader.readLine()) != null && !line.isEmpty()) {
                Task task = manager.fromString(line);
                int id = task.getId();
                if (id > maxId) {
                    maxId = id;
                }
                if (task.getClass().equals(Epic.class)) {
                    manager.epics.put(id, (Epic) task);
                } else if (task.getClass().equals(SubTask.class)) {
                    manager.subtasks.put(id, (SubTask) task);
                } else {
                    manager.tasks.put(id, task);
                }
            }
            manager.setIdentifier(maxId + 1);
            for (SubTask subTask : manager.getSubtasks()) {
                Epic epic = manager.epics.get(subTask.getEpicId());
                if (epic != null) {
                    epic.setSubTasks(subTask);
                }
            }

        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка при загрузке из файла", e);
        }

        return manager;
    }
}
