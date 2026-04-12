public class User {
    private String id;
    private String name;
    private String password;
    private String role;
    private String email;

    public User(String id, String name, String password, String role, String email) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    // Getter
    public String getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getEmail() { return email; }
}