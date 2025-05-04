package server.validation;

import entities.Task;

public class TaskValidator {
    public static String validate(Task task) {
        if (task.getName() == null || task.getName().isBlank()) {
            return "Имя задачи пустое";
        }
        if (task.getDescription() == null || task.getDescription().isBlank()) {
            return "Описание задачи пустое";
        }
        if (task.getStatus() == null) {
            return "Статус задачи не задан";
        }
        return null;
    }
}
