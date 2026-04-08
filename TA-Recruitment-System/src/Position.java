public class Position {
    private String id;
    private String moId;
    private String course;
    private String status;

    public Position(String id, String moId, String course, String status) {
        this.id = id;
        this.moId = moId;
        this.course = course;
        this.status = status;
    }

    // Getter
    public String getId() { return id; }
    public String getMoId() { return moId; }
    public String getCourse() { return course; }
    public String getStatus() { return status; }

    // Setter (用于修改状态)
    public void setStatus(String status) { this.status = status; }
}