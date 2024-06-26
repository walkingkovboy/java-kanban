import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.taskmanagers.FileBackedTaskManager;
import service.taskmanagers.Manager;
import service.taskmanagers.TaskManager;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        //sprint4(); //Изменил проверку
        //sprint5();
        //sprint6();
        //sprint7();
        sprint8();

    }

    public static void sprint4() {
        TaskManager tm = Manager.getDefaultTaskManager();
        Epic epic = new Epic("Эпик", "Первый эпик");
        SubTask subTask = new SubTask("Подзадача", "Первая подзадача", Status.NEW);
        SubTask subTask1 = new SubTask("Подзадача", "Вторая подзадача", Status.NEW);
        epic = tm.createEpic(epic);
        subTask = tm.createSubTask(subTask, epic.getId());
        subTask1 = tm.createSubTask(subTask1, epic.getId());
        System.out.println(tm.getSubTaskEpic(epic).size() + " Размер будет равен 2");//все две подзадачи в эпике
        //Попробуем изменить статусы подзадач и посмотреть на статусы эпика
        System.out.println(tm.getEpic(epic.getId()).getStatus() + " Текущий статус");
        subTask.setStatus(Status.DONE);
        tm.updateSubTask(subTask);
        System.out.println(tm.getEpic(epic.getId()).getStatus() + " Текущий статус");
        subTask1.setStatus(Status.DONE);
        tm.updateSubTask(subTask1);
        System.out.println(tm.getEpic(epic.getId()).getStatus() + " Текущий статус");
        System.out.println();
        //Получение эпика и подзадач, еще раз глянуть ТЗ, пришлось методы получения изменить, написано просто их распечатать
        System.out.println(tm.getSubTask(subTask1.getId()) + "Подзадача subTask1");
        System.out.println();
        tm.getSubTasksAll();
        System.out.println();
        System.out.println(tm.getEpic(epic.getId()) + " Наш эпик");
        System.out.println();
        //попробуем удалить одну подзадачу и посмотреть сколько у эпика подзадач
        tm.removeSubTask(subTask.getId());
        System.out.println(tm.getSubTaskEpic(epic).size() + " Количeство подзадач");
        System.out.println();
        //Попробуем удалить все подзадачи и посмотрим статус эпика
        tm.removeAllSubTasks();
        System.out.println(tm.getEpic(epic.getId()).getStatus() + " Текущий статус");
        System.out.println();

        //Попробуем теперь удалить эпик и посмотрим, что будет с подзадачами
        Epic epic1 = new Epic("Эпик", "Второй эпик");
        SubTask subTask2 = new SubTask("Подзадача", "Третья подзадача", Status.NEW);
        epic1 = tm.createEpic(epic1);
        subTask2 = tm.createSubTask(subTask2, epic1.getId());
        System.out.println(tm.getEpic(epic1.getId()));
        tm.removeEpic(epic1.getId());
        System.out.println(tm.getSubTask(subTask2.getId()));
        System.out.println(tm.getSubTask(subTask2.getId()).getEpic() + " Должно быть NULL");
    }

    public static void sprint5() {
        TaskManager tm = Manager.getDefaultTaskManager();
        //HistoryManager th = Manager.getDefaultHistory();
        Epic epic = new Epic("Эпик", "Первый эпик");
        SubTask subTask = new SubTask("Подзадача", "Первая подзадача", Status.NEW);
        SubTask subTask1 = new SubTask("Подзадача", "Вторая подзадача", Status.NEW);
        Task task = new Task("Обычная задача", "Треться", Status.NEW);
        epic = tm.createEpic(epic);
        subTask = tm.createSubTask(subTask, 0);
        subTask1 = tm.createSubTask(subTask1, 0);
        task = tm.createTask(task);
        tm.getEpic(0);
        tm.getSubTask(1);
        tm.getSubTask(2);
        tm.getSubTask(2);
        tm.getTask(3);
        tm.getEpic(0);
        //Вызвали 6 раз, посмотрим историю
        System.out.println(tm.getHistoryAll());
        System.out.println(tm.getHistoryAll().size());
        //Добавим еще 6 и посмотрим
        tm.getEpic(0);
        tm.getSubTask(1);
        tm.getSubTask(2);
        tm.getSubTask(2);
        tm.getTask(3);
        tm.getEpic(0);
        //Должна быть задача с айди 2
        System.out.println(tm.getHistoryAll());
    }

    public static void sprint6() {
        Task task = new Task("Обычная задача", "Первая", Status.NEW);
        Task task1 = new Task("Обычная задача", "Вторая", Status.NEW);
        SubTask subTask = new SubTask("Подзадача", "Первая подзадача", Status.NEW);
        SubTask subTask1 = new SubTask("Подзадача", "Вторая подзадача", Status.NEW);
        SubTask subTask2 = new SubTask("Подзадача", "Третья подзадача", Status.NEW);
        Epic epic = new Epic("Эпик", "Первый эпик");
        Epic epic1 = new Epic("Эпик", "Второй эпик");
        TaskManager tm = Manager.getDefaultTaskManager();
        System.out.println(tm.getHistoryAll());
        epic = tm.createEpic(epic);
        epic1 = tm.createEpic(epic1);
        subTask = tm.createSubTask(subTask, 0);
        subTask1 = tm.createSubTask(subTask1, 0);
        subTask2 = tm.createSubTask(subTask2, 0);
        task = tm.createTask(task);
        task1 = tm.createTask(task1);
        tm.getTask(task.getId());
        tm.getTask(task.getId());
        tm.getTask(task.getId());
        System.out.println(tm.getHistoryAll()); //Одна задача, при вызове get первой задачи
        tm.getEpic(epic.getId());
        tm.getEpic(epic.getId());
        tm.getEpic(epic1.getId());
        tm.getEpic(epic1.getId());
        tm.getEpic(epic.getId());
        tm.getTask(task1.getId());
        tm.getSubTask(subTask1.getId());
        tm.getSubTask(subTask1.getId());
        tm.getSubTask(subTask2.getId());
        tm.getSubTask(subTask.getId());
        tm.getSubTask(subTask1.getId());
        tm.getSubTask(subTask.getId());
        System.out.println(tm.getHistoryAll()); //Нет повторений
        System.out.println(tm.getHistoryAll().size() + " Размер до удаление");
        tm.removeTask(task.getId());
        tm.removeTask(task1.getId());
        System.out.println(tm.getHistoryAll().size());
        tm.removeSubTask(subTask1.getId());
        tm.removeSubTask(subTask2.getId());
        tm.removeSubTask(subTask.getId());
        System.out.println(tm.getHistoryAll().size());
        tm.removeEpic(epic.getId());
        tm.removeEpic(epic1.getId());
        System.out.println(tm.getHistoryAll().size());
    }

    public static void sprint7() {
        Path file = Paths.get(System.getProperty("user.dir"), "data", "data.csv");
        FileBackedTaskManager fm = new FileBackedTaskManager(file);
        //TaskManager tm = Manager.getDefaultTaskManager();
        Task task = new Task("Обычная задача", "Первая", Status.NEW);
        Task task1 = new Task("Обычная задача", "Вторая", Status.NEW);
        SubTask subTask = new SubTask("Подзадача", "Первая подзадача", Status.NEW);
        SubTask subTask1 = new SubTask("Подзадача", "Вторая подзадача", Status.NEW);
        SubTask subTask2 = new SubTask("Подзадача", "Третья подзадача", Status.NEW);
        Epic epic = new Epic("Эпик", "Первый эпик");
        Epic epic1 = new Epic("Эпик", "Второй эпик");
        epic = fm.createEpic(epic);
        epic1 = fm.createEpic(epic1);
        subTask = fm.createSubTask(subTask, 0);
        subTask1 = fm.createSubTask(subTask1, 0);
        subTask2 = fm.createSubTask(subTask2, 0);
        task = fm.createTask(task);
        task1 = fm.createTask(task1);
        fm.getTask(task.getId());
        fm.getTask(task.getId());
        fm.getTask(task.getId());
        fm.getEpic(epic.getId());
        fm.getEpic(epic.getId());
        fm.getEpic(epic1.getId());
        fm.getEpic(epic1.getId());
        fm.getEpic(epic.getId());
        fm.getTask(task1.getId());
        fm.getSubTask(subTask1.getId());
        fm.getSubTask(subTask1.getId());
        fm.getSubTask(subTask2.getId());
        fm.getSubTask(subTask.getId());
        fm.getSubTask(subTask1.getId());
        fm.getSubTask(subTask.getId());
        System.out.println(fm.getEpicsAll());
        System.out.println(fm.getTasksAll());
        System.out.println(fm.getSubTasksAll());
        System.out.println("История:" + fm.getHistoryAll() + "\n");
        FileBackedTaskManager fm2 = FileBackedTaskManager.loadFromFile(file);
        System.out.println(fm.getEpicsAll());
        System.out.println(fm.getTasksAll());
        System.out.println(fm.getSubTasksAll());
        System.out.println("История:" + fm2.getHistoryAll() + "\n");

    }

    static void sprint8() {
        Path file = Paths.get(System.getProperty("user.dir"), "data", "data.csv");
        FileBackedTaskManager fm = new FileBackedTaskManager(file);
        Task task = new Task("Обычная задача", "Первая", Status.NEW);
        task = fm.createTask(task);
    }
}

