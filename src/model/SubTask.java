package model;

public class SubTask extends Task {
    private Epic epic; //исправил

    public SubTask(String name, String description, Status status) {
        super(name, description, status);
    }

    public SubTask(String name, String description, Status status, int id) {
        super(name, description, status, id);
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    @Override
    public String getType() {
        return "SUBTASK";
    }

    @Override
    public String toString() {
        return String.format("%s,%s,%s,%s,%s,%s", this.getType(), this.getId(), this.getTitle(), this.getStatus(),
                this.getDescription(), this.getEpic().getId());
    }

}
