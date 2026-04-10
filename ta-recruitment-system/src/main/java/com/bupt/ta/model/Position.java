package com.bupt.ta.model;

import java.util.ArrayList;
import java.util.List;

public class Position {
    public enum Status { PENDING_APPROVAL, APPROVED, REJECTED, CLOSED }

    private String id;
    private String courseName;
    private String courseCode;
    private String department;
    private int numPositions;
    private int hoursPerWeek;
    private String payRate;
    private String deadline;
    private List<String> requiredSkills;
    private String duties;
    private String preferredQualifications;
    private String moId;
    private String moName;
    private Status status;

    public Position() {
        this.requiredSkills = new ArrayList<>();
        this.status = Status.PENDING_APPROVAL;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public int getNumPositions() { return numPositions; }
    public void setNumPositions(int numPositions) { this.numPositions = numPositions; }
    public int getHoursPerWeek() { return hoursPerWeek; }
    public void setHoursPerWeek(int hoursPerWeek) { this.hoursPerWeek = hoursPerWeek; }
    public String getPayRate() { return payRate; }
    public void setPayRate(String payRate) { this.payRate = payRate; }
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    public List<String> getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(List<String> requiredSkills) { this.requiredSkills = requiredSkills; }
    public String getDuties() { return duties; }
    public void setDuties(String duties) { this.duties = duties; }
    public String getPreferredQualifications() { return preferredQualifications; }
    public void setPreferredQualifications(String preferredQualifications) { this.preferredQualifications = preferredQualifications; }
    public String getMoId() { return moId; }
    public void setMoId(String moId) { this.moId = moId; }
    public String getMoName() { return moName; }
    public void setMoName(String moName) { this.moName = moName; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}
