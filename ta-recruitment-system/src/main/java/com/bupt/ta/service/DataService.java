package com.bupt.ta.service;

import com.bupt.ta.model.Application;
import com.bupt.ta.model.Position;
import com.bupt.ta.model.User;
import com.bupt.ta.util.JsonStorage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class DataService {
    private static DataService instance;
    private List<User> users;
    private List<Position> positions;
    private List<Application> applications;
    private User currentUser;

    private static final String USERS_FILE = "users.json";
    private static final String POSITIONS_FILE = "positions.json";
    private static final String APPLICATIONS_FILE = "applications.json";

    private DataService() {
        users = JsonStorage.loadList(USERS_FILE, User.class);
        positions = JsonStorage.loadList(POSITIONS_FILE, Position.class);
        applications = JsonStorage.loadList(APPLICATIONS_FILE, Application.class);
        if (users.isEmpty()) {
            initSampleData();
        }
    }

    public static DataService getInstance() {
        if (instance == null) {
            instance = new DataService();
        }
        return instance;
    }

    private void initSampleData() {
        User admin = new User("admin-001", "System Admin", "admin@bupt.edu.cn", "admin123", User.Role.ADMIN);
        User mo1 = new User("mo-001", "Prof. Li Ming", "li.ming@bupt.edu.cn", "mo123", User.Role.MO);
        User mo2 = new User("mo-002", "Prof. Wang Yi", "wang.yi@bupt.edu.cn", "mo123", User.Role.MO);
        User ta1 = new User("ta-001", "Zhang Wei", "zhangwei@bupt.edu.cn", "ta123", User.Role.TA);
        ta1.setStudentId("2024213001");
        ta1.setProgramme("Computer Science");
        ta1.setYearOfStudy("Year 3");
        ta1.setSkills(Arrays.asList("Java", "Python", "Data Structures", "SQL"));
        ta1.setUniversity("Beijing University of Posts and Telecom");
        ta1.setGpa("3.7 / 4.0");
        ta1.setCvFileName("CV_ZhangWei_2026.pdf");
        ta1.setExperience("Assisted in EBU5302 lab sessions for one semester.");

        User ta2 = new User("ta-002", "Liu Yang", "liuyang@bupt.edu.cn", "ta123", User.Role.TA);
        ta2.setStudentId("2024213002");
        ta2.setProgramme("Computer Science");
        ta2.setYearOfStudy("Year 2");
        ta2.setSkills(Arrays.asList("Java", "SQL"));
        ta2.setUniversity("Beijing University of Posts and Telecom");
        ta2.setGpa("3.5 / 4.0");

        User ta3 = new User("ta-003", "Wang Jun", "wangjun@bupt.edu.cn", "ta123", User.Role.TA);
        ta3.setStudentId("2024213003");
        ta3.setProgramme("Software Engineering");
        ta3.setYearOfStudy("Year 3");
        ta3.setSkills(Arrays.asList("Java", "Testing", "UML"));
        ta3.setUniversity("Beijing University of Posts and Telecom");
        ta3.setGpa("3.8 / 4.0");

        users.addAll(Arrays.asList(admin, mo1, mo2, ta1, ta2, ta3));

        Position p1 = new Position();
        p1.setId("POS-001");
        p1.setCourseName("Software Engineering");
        p1.setCourseCode("EBU6304");
        p1.setDepartment("International School");
        p1.setNumPositions(3);
        p1.setHoursPerWeek(8);
        p1.setPayRate("¥50/hr");
        p1.setDeadline("2026-04-05");
        p1.setRequiredSkills(Arrays.asList("Java", "Agile", "Testing", "UML"));
        p1.setDuties("- Assist students during lab sessions\n- Grade assignments and provide feedback\n- Hold weekly office hours\n- Support exam invigilation");
        p1.setPreferredQualifications("- Year 3 or above\n- Previous TA experience preferred\n- Strong communication skills");
        p1.setMoId("mo-001");
        p1.setMoName("Prof. Li Ming");
        p1.setStatus(Position.Status.APPROVED);

        Position p2 = new Position();
        p2.setId("POS-002");
        p2.setCourseName("Data Structures");
        p2.setCourseCode("EBU5302");
        p2.setDepartment("International School");
        p2.setNumPositions(2);
        p2.setHoursPerWeek(6);
        p2.setPayRate("¥50/hr");
        p2.setDeadline("2026-04-10");
        p2.setRequiredSkills(Arrays.asList("C/C++", "Algorithms"));
        p2.setDuties("- Lead tutorial sessions\n- Grade programming assignments\n- Prepare exercise materials");
        p2.setMoId("mo-001");
        p2.setMoName("Prof. Li Ming");
        p2.setStatus(Position.Status.APPROVED);

        Position p3 = new Position();
        p3.setId("POS-003");
        p3.setCourseName("Database Systems");
        p3.setCourseCode("EBU5304");
        p3.setDepartment("International School");
        p3.setNumPositions(1);
        p3.setHoursPerWeek(4);
        p3.setPayRate("¥50/hr");
        p3.setDeadline("2026-03-28");
        p3.setRequiredSkills(Arrays.asList("SQL", "ER Diagram"));
        p3.setDuties("- Help students with SQL exercises\n- Grade lab assignments");
        p3.setMoId("mo-002");
        p3.setMoName("Prof. Wang Yi");
        p3.setStatus(Position.Status.APPROVED);

        Position p4 = new Position();
        p4.setId("POS-004");
        p4.setCourseName("Exam Invigilation");
        p4.setCourseCode("General");
        p4.setDepartment("International School");
        p4.setNumPositions(10);
        p4.setHoursPerWeek(0);
        p4.setPayRate("¥60/hr");
        p4.setDeadline("2026-05-01");
        p4.setRequiredSkills(Arrays.asList("Invigilation", "No Prerequisites"));
        p4.setDuties("- Monitor exam halls\n- Distribute and collect exam papers\n- Report irregularities");
        p4.setMoId("mo-001");
        p4.setMoName("Prof. Li Ming");
        p4.setStatus(Position.Status.APPROVED);

        Position p5 = new Position();
        p5.setId("POS-005");
        p5.setCourseName("Computer Networks");
        p5.setCourseCode("EBU5306");
        p5.setDepartment("International School");
        p5.setNumPositions(2);
        p5.setHoursPerWeek(6);
        p5.setPayRate("¥50/hr");
        p5.setDeadline("2026-04-15");
        p5.setRequiredSkills(Arrays.asList("TCP/IP", "Networking", "Wireshark"));
        p5.setDuties("- Assist with network lab sessions\n- Help troubleshoot student setups");
        p5.setMoId("mo-001");
        p5.setMoName("Prof. Li Ming");
        p5.setStatus(Position.Status.PENDING_APPROVAL);

        Position p6 = new Position();
        p6.setId("POS-006");
        p6.setCourseName("AI Fundamentals");
        p6.setCourseCode("EBU6308");
        p6.setDepartment("International School");
        p6.setNumPositions(2);
        p6.setHoursPerWeek(6);
        p6.setPayRate("¥50/hr");
        p6.setDeadline("2026-04-20");
        p6.setRequiredSkills(Arrays.asList("Python", "Machine Learning", "TensorFlow"));
        p6.setDuties("- Assist in AI lab sessions\n- Help students with Python ML projects");
        p6.setMoId("mo-002");
        p6.setMoName("Prof. Wang Yi");
        p6.setStatus(Position.Status.PENDING_APPROVAL);

        positions.addAll(Arrays.asList(p1, p2, p3, p4, p5, p6));

        Application a1 = new Application();
        a1.setId("APP-001");
        a1.setTaId("ta-001");
        a1.setTaName("Zhang Wei");
        a1.setPositionId("POS-001");
        a1.setPositionTitle("Software Engineering TA");
        a1.setCourseCode("EBU6304");
        a1.setAppliedDate("2026-03-20");
        a1.setStatus(Application.Status.PASSED);
        a1.setFeedback("Excellent skills match. Welcome aboard!");

        Application a2 = new Application();
        a2.setId("APP-002");
        a2.setTaId("ta-001");
        a2.setTaName("Zhang Wei");
        a2.setPositionId("POS-002");
        a2.setPositionTitle("Data Structures TA");
        a2.setCourseCode("EBU5302");
        a2.setAppliedDate("2026-03-18");
        a2.setStatus(Application.Status.PENDING);
        a2.setFeedback("Under review");

        Application a3 = new Application();
        a3.setId("APP-003");
        a3.setTaId("ta-001");
        a3.setTaName("Zhang Wei");
        a3.setPositionId("POS-003");
        a3.setPositionTitle("Database Systems TA");
        a3.setCourseCode("EBU5304");
        a3.setAppliedDate("2026-03-15");
        a3.setStatus(Application.Status.FAILED);
        a3.setFeedback("Position filled. Consider applying next semester.");

        Application a4 = new Application();
        a4.setId("APP-004");
        a4.setTaId("ta-002");
        a4.setTaName("Liu Yang");
        a4.setPositionId("POS-001");
        a4.setPositionTitle("Software Engineering TA");
        a4.setCourseCode("EBU6304");
        a4.setAppliedDate("2026-03-19");
        a4.setStatus(Application.Status.PENDING);

        Application a5 = new Application();
        a5.setId("APP-005");
        a5.setTaId("ta-003");
        a5.setTaName("Wang Jun");
        a5.setPositionId("POS-001");
        a5.setPositionTitle("Software Engineering TA");
        a5.setCourseCode("EBU6304");
        a5.setAppliedDate("2026-03-18");
        a5.setStatus(Application.Status.PENDING);

        applications.addAll(Arrays.asList(a1, a2, a3, a4, a5));

        saveAll();
    }

    public void saveAll() {
        JsonStorage.save(USERS_FILE, users);
        JsonStorage.save(POSITIONS_FILE, positions);
        JsonStorage.save(APPLICATIONS_FILE, applications);
    }

    // === Auth ===
    public User login(String email, String password, User.Role role) {
        return users.stream()
                .filter(u -> u.getEmail().equals(email) && u.getPassword().equals(password) && u.getRole() == role)
                .findFirst().orElse(null);
    }

    public boolean registerTA(User user) {
        boolean exists = users.stream().anyMatch(u -> u.getEmail().equals(user.getEmail()));
        if (exists) return false;
        user.setId("ta-" + String.format("%03d", users.stream().filter(u -> u.getRole() == User.Role.TA).count() + 1));
        user.setRole(User.Role.TA);
        users.add(user);
        saveAll();
        return true;
    }

    public User getCurrentUser() { return currentUser; }
    public void setCurrentUser(User user) { this.currentUser = user; }

    public void updateUser(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                break;
            }
        }
        saveAll();
    }

    // === Positions ===
    public List<Position> getAllPositions() { return new ArrayList<>(positions); }

    public List<Position> getApprovedPositions() {
        return positions.stream()
                .filter(p -> p.getStatus() == Position.Status.APPROVED)
                .collect(Collectors.toList());
    }

    public List<Position> getPositionsByMo(String moId) {
        return positions.stream().filter(p -> p.getMoId().equals(moId)).collect(Collectors.toList());
    }

    public List<Position> getPendingPositions() {
        return positions.stream()
                .filter(p -> p.getStatus() == Position.Status.PENDING_APPROVAL)
                .collect(Collectors.toList());
    }

    public Position getPositionById(String id) {
        return positions.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    public void addPosition(Position position) {
        position.setId("POS-" + String.format("%03d", positions.size() + 1));
        positions.add(position);
        saveAll();
    }

    public void updatePosition(Position position) {
        for (int i = 0; i < positions.size(); i++) {
            if (positions.get(i).getId().equals(position.getId())) {
                positions.set(i, position);
                break;
            }
        }
        saveAll();
    }

    // === Applications ===
    public List<Application> getAllApplications() { return new ArrayList<>(applications); }

    public List<Application> getApplicationsByTa(String taId) {
        return applications.stream().filter(a -> a.getTaId().equals(taId)).collect(Collectors.toList());
    }

    public List<Application> getApplicationsByPosition(String positionId) {
        return applications.stream().filter(a -> a.getPositionId().equals(positionId)).collect(Collectors.toList());
    }

    public boolean hasApplied(String taId, String positionId) {
        return applications.stream().anyMatch(a -> a.getTaId().equals(taId) && a.getPositionId().equals(positionId));
    }

    public void addApplication(Application app) {
        app.setId("APP-" + String.format("%03d", applications.size() + 1));
        applications.add(app);
        saveAll();
    }

    public void updateApplication(Application app) {
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getId().equals(app.getId())) {
                applications.set(i, app);
                break;
            }
        }
        saveAll();
    }

    public User getUserById(String id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);
    }

    public List<User> getAllTAs() {
        return users.stream().filter(u -> u.getRole() == User.Role.TA).collect(Collectors.toList());
    }

    public String getTodayDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public int countApplicationsByStatus(Application.Status status) {
        return (int) applications.stream().filter(a -> a.getStatus() == status).count();
    }

    public int countHiredTAsForPosition(String positionId) {
        return (int) applications.stream()
                .filter(a -> a.getPositionId().equals(positionId) && a.getStatus() == Application.Status.PASSED)
                .count();
    }

    public boolean changePassword(String userId, String oldPassword, String newPassword) {
        User user = getUserById(userId);
        if (user != null && user.getPassword().equals(oldPassword)) {
            user.setPassword(newPassword);
            saveAll();
            return true;
        }
        return false;
    }
}
