package com.bupt.ta.model;

import com.bupt.ta.util.CvStorage;

import java.util.ArrayList;
import java.util.List;

public class User {
    public enum Role { TA, MO, ADMIN }

    private String id;
    private String name;
    private String email;
    private String studentId;
    private String phone;
    private String password;
    private Role role;

    // TA-specific fields
    private String programme;
    private String yearOfStudy;
    private List<String> skills;
    private String experience;
    /** Original file name for display (e.g. MyResume.pdf). */
    private String cvFileName;
    /** Path relative to the data directory, e.g. cvs/ta-001_MyResume.pdf */
    private String cvStoragePath;
    private String university;
    private String gpa;

    public User() {
        this.skills = new ArrayList<>();
    }

    public User(String id, String name, String email, String password, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.skills = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public String getProgramme() { return programme; }
    public void setProgramme(String programme) { this.programme = programme; }
    public String getYearOfStudy() { return yearOfStudy; }
    public void setYearOfStudy(String yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    public String getCvFileName() { return cvFileName; }
    public void setCvFileName(String cvFileName) { this.cvFileName = cvFileName; }
    public String getCvStoragePath() { return cvStoragePath; }
    public void setCvStoragePath(String cvStoragePath) { this.cvStoragePath = cvStoragePath; }

    public boolean hasCvOnDisk() {
        return cvStoragePath != null && !cvStoragePath.isBlank() && CvStorage.storedFileExists(cvStoragePath);
    }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public String getGpa() { return gpa; }
    public void setGpa(String gpa) { this.gpa = gpa; }
}
