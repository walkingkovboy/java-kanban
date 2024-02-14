package Model;

public class SubTask extends Task {
    private Epic epic; //исправил

    public SubTask(String name, String description, Status status) {
        super(name, description, status);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
            return epic;
    }
}
