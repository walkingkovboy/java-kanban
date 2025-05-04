package server.validation;

import entities.SubTask;

public class SubTaskValidator {
    public static String validate(SubTask st) {
        if (st.getName() == null || st.getName().isBlank()) {
            return "Имя подзадачи пустое";
        }
        if (st.getDescription() == null || st.getDescription().isBlank()) {
            return "Описание подзадачи пустое";
        }
        if (st.getStatus() == null) {
            return "Статус подзадачи не задан";
        }
        if (st.getEpicId() < 0) {
            return "Неверный epicId";
        }
        return null;
    }
}