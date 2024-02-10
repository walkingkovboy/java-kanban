package Model;

public class SubTask extends Task {
    private Epic epic; //исправил

    public SubTask(Task task) {
        super(task.getTitle(), task.getDescription(), task.getStatus());
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        if (epic == null) {
            return null;
        } else {
            return epic;
        }

    }
}
