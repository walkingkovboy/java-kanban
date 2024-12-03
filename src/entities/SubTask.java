package entities;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(String name, String description, Status status) {
        super(name, description, status);
    }

    public SubTask(String name, String description, int id, Status status, Epic epic) {
        super(name, description, id, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }

    @Override
    public String toString() {
        String result = "SubTask{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status;
        if (epic != null) {
            result = result + ", epicId = " + epic.getId();
        } else if (epic == null) {
            result = result + ", epicId=null";
        }
        return result + "}";
    }
}
