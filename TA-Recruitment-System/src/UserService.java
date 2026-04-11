import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class UserService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private static final String FILE = "data/users.txt";

    public User login(String userId, String password) {
        // 参数非空校验
        if (userId == null || password == null) {
            logger.warning("Login failed: userId or password is null");
            return null;
        }
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getId().equals(userId) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public boolean register(User user) {
        if (user == null) {
            logger.warning("Register failed: user is null");
            return false;
        }
        List<User> users = getAllUsers();

        for (User u : users) {
            if (u.getId().equals(user.getId()) || u.getEmail().equals(user.getEmail())) {
                logger.info("Register failed: userId or email duplicate, userId=" + user.getId() + ", email=" + user.getEmail());
                return false;
            }
        }
        List<String> lines = FileUtil.read(FILE);
        lines.add(user.toLine());
        FileUtil.write(FILE, lines);
        logger.info("Register success: userId=" + user.getId());
        return true;
    }


    public List<User> getAllUsers() {
        List<String> lines = FileUtil.read(FILE);
        List<User> list = new ArrayList<>();
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            User u = User.fromLine(line);
            if (u != null) {
                list.add(u);
            } else {
                logger.warning("Parse user failed: invalid line - " + line);
            }
        }
        return list;
    }

    public List<User> getAllTAs() {
        List<User> all = getAllUsers();
        List<User> tas = new ArrayList<>();
        for (User u : all) {
            if ("TA".equals(u.getRole())) {
                tas.add(u);
            }
        }
        return tas;
    }


    public User getUserById(String id) {
        if (id == null || id.trim().isEmpty()) {
            logger.warning("Get user failed: id is empty");
            return null;
        }
        for (User u : getAllUsers()) {
            if (u.getId().equals(id)) {
                return u;
            }
        }
        logger.info("User not found: id=" + id);
        return null;
    }

    /**
     * 更新用户信息（根据ID匹配）
     * @param updated 待更新的用户对象
     */
    public void updateUser(User updated) {
        if (updated == null) {
            logger.warning("Update user failed: updated user is null");
            return;
        }
        List<User> users = getAllUsers();
        List<String> lines = new ArrayList<>();
        boolean isUpdated = false;
        for (User u : users) {
            if (u.getId().equals(updated.getId())) {
                lines.add(updated.toLine());
                isUpdated = true;
            } else {
                lines.add(u.toLine());
            }
        }
        FileUtil.write(FILE, lines);
        if (isUpdated) {
            logger.info("Update user success: userId=" + updated.getId());
        } else {
            logger.warning("Update user failed: user not found, userId=" + updated.getId());
        }
    }


    public boolean changePassword(String userId, String oldPwd, String newPwd) {
        if (userId == null || oldPwd == null || newPwd == null) {
            logger.warning("Change password failed: param is null");
            return false;
        }
        User user = getUserById(userId);
        if (user == null || !user.getPassword().equals(oldPwd)) {
            logger.warning("Change password failed: user not found or old password incorrect, userId=" + userId);
            return false;
        }
        user.setPassword(newPwd);
        updateUser(user);
        logger.info("Change password success: userId=" + userId);
        return true;
    }


    public int generateTAId() {
        List<User> tas = getAllTAs();
        return tas.size() + 1;
    }
}