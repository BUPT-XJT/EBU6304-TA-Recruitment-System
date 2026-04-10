public class User {
    private String id;
    private String name;
    private String password;
    private String role;       // TA, MO, ADMIN
    private String email;
    private String phone;
    private String programme;
    private String yearOfStudy;
    private String skills;     // semicolon-separated, e.g. "Java;Python;SQL"
    private String experience;
    private String cvFileName;
    private String university;
    private String gpa;

    public User(String id, String name, String password, String role, String email) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = "";
        this.programme = "";
        this.yearOfStudy = "";
        this.skills = "";
        this.experience = "";
        this.cvFileName = "";
        this.university = "";
        this.gpa = "";
    }

    public User(String id, String name, String password, String role, String email,
                String phone, String programme, String yearOfStudy, String skills,
                String experience, String cvFileName, String university, String gpa) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
        this.email = email;
        this.phone = phone;
        this.programme = programme;
        this.yearOfStudy = yearOfStudy;
        this.skills = skills;
        this.experience = experience;
        this.cvFileName = cvFileName;
        this.university = university;
        this.gpa = gpa;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getProgramme() { return programme; }
    public String getYearOfStudy() { return yearOfStudy; }
    public String getSkills() { return skills; }
    public String getExperience() { return experience; }
    public String getCvFileName() { return cvFileName; }
    public String getUniversity() { return university; }
    public String getGpa() { return gpa; }

    public void setName(String name) { this.name = name; }
    public void setPassword(String password) { this.password = password; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setProgramme(String programme) { this.programme = programme; }
    public void setYearOfStudy(String yearOfStudy) { this.yearOfStudy = yearOfStudy; }
    public void setSkills(String skills) { this.skills = skills; }
    public void setExperience(String experience) { this.experience = experience; }
    public void setCvFileName(String cvFileName) { this.cvFileName = cvFileName; }
    public void setUniversity(String university) { this.university = university; }
    public void setGpa(String gpa) { this.gpa = gpa; }

    public String toLine() {
        return String.join("|", id, name, password, role, email, phone,
                programme, yearOfStudy, skills, experience, cvFileName, university, gpa);
    }

    public static User fromLine(String line) {
        String[] s = line.split("\\|", -1);
        if (s.length < 5) return null;
        return new User(
                s[0], s[1], s[2], s[3], s[4],
                s.length > 5 ? s[5] : "",
                s.length > 6 ? s[6] : "",
                s.length > 7 ? s[7] : "",
                s.length > 8 ? s[8] : "",
                s.length > 9 ? s[9] : "",
                s.length > 10 ? s[10] : "",
                s.length > 11 ? s[11] : "",
                s.length > 12 ? s[12] : ""
        );
    }
}
