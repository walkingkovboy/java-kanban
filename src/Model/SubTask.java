package Model;

public class SubTask extends Task {
    private Epic epic; //исправил

    public SubTask(Task task, Epic epic) {
        super(task.getTitle(), task.getDescription(), task.getStatus());
        this.epic = epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }
}
