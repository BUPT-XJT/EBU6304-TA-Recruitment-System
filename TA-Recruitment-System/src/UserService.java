import java.util.List;

public class UserService {
    public User login(String userId, String password) {
        List<String> lines = FileUtil.read("data/users.txt");
        for (String line : lines) {
            String[] s = line.split(",");
            if (s.length >= 5 && s[0].equals(userId) && s[2].equals(password)) {
                return new User(s[0], s[1], s[2], s[3], s[4]);
            }
        }
        return null;
    }
}