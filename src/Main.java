import Model.Epic;
import Model.Status;
import Model.SubTask;
import Model.Task;
import Service.TaskManager;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {

        TaskManager tm = new TaskManager();
        Epic epic = tm.createEpic(new Epic(new Task("Эпик", "Первый эпик", Status.NEW), new ArrayList<SubTask>()));//СОздали Эпик
        SubTask subTask = tm.createSubTask(new SubTask(new Task("Подзадача", "Первая подзадача", Status.NEW), epic));//Создали подзадачу и связали сразу с эпиком
        SubTask subTask1 = tm.createSubTask(new SubTask(new Task("Подзадача", "Вторая подзадача", Status.NEW), epic));
        SubTask subTask2 = tm.createSubTask(new SubTask(new Task("Подзадача", "Третья подзадача", Status.NEW), epic));
        //создали еще подзхадач, проверим есть ли они в нашем эпике
        System.out.println(tm.getSubTaskEpic(epic).size());//все три подзадачи в эпике
        //Попробуем изменить статусы подзадач и посмотреть на статусы эпика
        System.out.println(tm.getEpic(epic.getId()).getStatus() + " Текущий статус");
        subTask.setStatus(Status.DONE);
        tm.updateSubTask(subTask);
        System.out.println(tm.getEpic(epic.getId()).getStatus() + " Текущий статус");
        subTask1.setStatus(Status.DONE);
        tm.updateSubTask(subTask1);
        System.out.println(tm.getEpic(epic.getId()).getStatus() + " Текущий статус");
        subTask2.setStatus(Status.DONE);
        tm.updateSubTask(subTask2);
        System.out.println(tm.getEpic(epic.getId()).getStatus() + " Текущий статус");
        System.out.println();
        //Получение эпика и подзадач, еще раз глянуть ТЗ, пришлось методы получения изменить, написано просто их распечатать
        System.out.println(tm.getSubTask(subTask1.getId()) + "Подзадача subTask1");
        System.out.println();
        tm.allGetSubTasks();
        System.out.println();
        System.out.println(tm.getEpic(epic.getId()) + "Наш эпик");
        System.out.println();
        //попробуем удалить одну подзадачу и посмотреть сколько у эпика подзадач
        tm.removeSubTask(subTask1.getId());
        System.out.println(tm.getSubTaskEpic(epic).size() + " Колиечство подзадач");
        System.out.println();
        //Попробуем удалить все подзадачи и посмотрим статус эпика
        tm.removeAllSubTasks();
        System.out.println(tm.getEpic(epic.getId()).getStatus() + " Текущий статус");
        System.out.println();

        //Попробуем теперь удалить эпик и посмотрим, что будет с подзадачами
        Epic epic1 = tm.createEpic(new Epic(new Task("Эпик1", "Второй эпик", Status.NEW), new ArrayList<SubTask>()));
        SubTask subTask4 = tm.createSubTask(new SubTask(new Task("Подзадача", "Первая подзадача", Status.NEW), epic1));
        System.out.println(tm.getEpic(epic1.getId()));
        tm.removeEpic(epic1.getId());
        System.out.println(tm.getSubTask(subTask4.getId()).getEpic() + " Должно быть NULL");

    }
}
