package server.validation;

import entities.Epic;

public class EpicValidator {
    public static String validate(Epic epic) {
        if (epic.getName() == null || epic.getName().isBlank()) {
            return "Имя эпика пустое";
        }
        if (epic.getDescription() == null || epic.getDescription().isBlank()) {
            return "Описание эпика пустое";
        }
        return null;
    }
}
