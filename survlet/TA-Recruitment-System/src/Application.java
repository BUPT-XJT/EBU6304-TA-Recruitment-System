public class Application {
    private String id;
    private String taId;
    private String positionId;
    private String status;
    private String feedback;

    public Application(String id, String taId, String positionId, String status, String feedback) {
        this.id = id;
        this.taId = taId;
        this.positionId = positionId;
        this.status = status;
        this.feedback = feedback;
    }

    // Getter
    public String getId() { return id; }
    public String getTaId() { return taId; }
    public String getPositionId() { return positionId; }
    public String getStatus() { return status; }
    public String getFeedback() { return feedback; }

    // Setter
    public void setStatus(String status) { this.status = status; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}