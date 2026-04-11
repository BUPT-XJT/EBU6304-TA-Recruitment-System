package com.bupt.ta.model;

public class Application {
    public enum Status { PENDING, PASSED, FAILED }

    private String id;
    private String taId;
    private String taName;
    private String positionId;
    private String positionTitle;
    private String courseCode;
    private String appliedDate;
    private String additionalNotes;
    private Status status;
    private String feedback;

    public Application() {
        this.status = Status.PENDING;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }
    public String getTaName() { return taName; }
    public void setTaName(String taName) { this.taName = taName; }
    public String getPositionId() { return positionId; }
    public void setPositionId(String positionId) { this.positionId = positionId; }
    public String getPositionTitle() { return positionTitle; }
    public void setPositionTitle(String positionTitle) { this.positionTitle = positionTitle; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getAppliedDate() { return appliedDate; }
    public void setAppliedDate(String appliedDate) { this.appliedDate = appliedDate; }
    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}
