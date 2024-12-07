import Manager.HistoryManager;
import Manager.InMemoryTaskManager;
import Manager.Managers;
import Manager.TaskManager;
import entities.Epic;
import entities.Status;
import entities.SubTask;
import entities.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager tm = Managers.getDefault();
        HistoryManager hystoryManager = Managers.getDefaultHistory();
        System.out.println("Создадим два эпика");
        tm.addEpic(new Epic("Эпик1", "Описание первого эпика"));
        tm.addEpic(new Epic("Эпик2", "Описание второго эпика"));
        System.out.println("Создадим 3 подзадачи");
        tm.getEpic(0);
        tm.getEpic(0);
        
        tm.addSubtask(new SubTask("Подзадача1", "Описание 1 подзадачи", Status.NEW), 0);
        tm.addSubtask(new SubTask("Подзадача2", "Описание 2 подзадачи", Status.IN_PROGRESS), 0);
        tm.addSubtask(new SubTask("Подзадача3", "Описание 3 подзадачи", Status.DONE), 0);
        System.out.println("Создадим 2 задачи");
        tm.addTask(new Task("Задача 1", "Описание 1 задачи", Status.DONE));
        tm.addTask(new Task("Задача 2", "Описание 2 задачи", Status.IN_PROGRESS));
        System.out.println("Посмотрим, что там с эпиком у которого три подзадачи");
        tm.getTask(5);
        System.out.println(hystoryManager.getHistory().size());
        System.out.println(tm.getEpics());
        System.out.println("Посмотрим, что там с подзадачами и задачами");
        System.out.println(tm.getSubtasks());
        System.out.println(tm.getTasks());
        System.out.println("Посмотрим что будет если все три задачи обвноить и сделать статус DONE");
        SubTask subTask = new SubTask("Подзадача1", "Описание теперь другое", 2, Status.DONE, tm.getEpic(0));
        SubTask subTask1 = new SubTask("Подзадача2", "Здесь тоже новое описание", 3, Status.DONE, tm.getEpic(0));
        SubTask subTask2 = new SubTask("Подзадача 3 и новое ее название", "Почему бы и здесь так не сделать", 4, Status.DONE, tm.getEpic(0));
        tm.updateSubtask(subTask);
        tm.updateSubtask(subTask1);
        tm.updateSubtask(subTask2);
        System.out.println(tm.getSubtasks());
        System.out.println("Посмотрим, что там с эпиком у которого три подзадачи");
        System.out.println(tm.getEpics());
        System.out.println("Выведем все подзадачи первого и второго эпика");
        System.out.println(tm.getSubtasksEpic(0));
        System.out.println("********************");
        System.out.println(tm.getSubtasksEpic(1));
        System.out.println("Удалим теперь все подзадачи и посмотрим что с эпиком будет");
        tm.removeAllSubtask();
        System.out.println(tm.getEpic(0));
        System.out.println("Удалим Эпик и посмотрим сколько у нас эпиков");
        tm.removeEpic(0);
        System.out.println(tm.getEpics());

    }
}
