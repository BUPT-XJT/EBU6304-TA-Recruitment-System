package com.bupt.ta.web;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static String usersFile() {
        return ProjectPaths.dataFile("users.txt");
    }

    private static boolean diskUsersLooksEmpty(List<String> lines) {
        for (String s : lines) {
            if (!s.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private static List<String> readUsersLinesFromDisk() {
        return FileUtil.read(usersFile());
    }

    /** Same demo accounts as {@code src/main/resources/data/users.txt} when disk file is missing. */
    private static List<String> readSeedUsersFromClasspath() {
        List<String> out = new ArrayList<>();
        try (InputStream in = UserService.class.getResourceAsStream("/data/users.txt")) {
            if (in == null) {
                return out;
            }
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String t = line.trim();
                    if (!t.isEmpty()) {
                        out.add(t);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[UserService] classpath seed users failed: " + e.getMessage());
        }
        return out;
    }

    private static List<String> loadUserLines() {
        List<String> fromDisk = readUsersLinesFromDisk();
        Path p = Paths.get(usersFile());
        if (!Files.isRegularFile(p) || diskUsersLooksEmpty(fromDisk)) {
            List<String> seed = readSeedUsersFromClasspath();
            if (!seed.isEmpty()) {
                return seed;
            }
        }
        return fromDisk;
    }

    public User login(String userId, String password) {
        List<User> users = listUsers();
        for (User u : users) {
            if (u.getId().equals(userId) && u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public boolean register(User user) {
        List<User> users = listUsers();
        for (User u : users) {
            if (u.getId().equals(user.getId()) || u.getEmail().equals(user.getEmail())) {
                return false;
            }
        }
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            lines.add(u.toLine());
        }
        lines.add(user.toLine());
        FileUtil.write(usersFile(), lines);
        return true;
    }

    public List<User> getAllUsers() {
        return listUsers();
    }

    private static List<User> listUsers() {
        List<String> lines = loadUserLines();
        List<User> list = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty()) {
                continue;
            }
            User u = User.fromLine(line);
            if (u != null) {
                list.add(u);
            }
        }
        return list;
    }

    public List<User> getAllTAs() {
        List<User> all = getAllUsers();
        List<User> tas = new ArrayList<>();
        for (User u : all) {
            if ("TA".equals(u.getRole())) tas.add(u);
        }
        return tas;
    }

    public User getUserById(String id) {
        for (User u : getAllUsers()) {
            if (u.getId().equals(id)) return u;
        }
        return null;
    }

    public void updateUser(User updated) {
        List<User> users = listUsers();
        List<String> lines = new ArrayList<>();
        for (User u : users) {
            if (u.getId().equals(updated.getId())) {
                lines.add(updated.toLine());
            } else {
                lines.add(u.toLine());
            }
        }
        FileUtil.write(usersFile(), lines);
    }

    public boolean changePassword(String userId, String oldPwd, String newPwd) {
        User user = getUserById(userId);
        if (user == null || !user.getPassword().equals(oldPwd)) return false;
        user.setPassword(newPwd);
        updateUser(user);
        return true;
    }

    public int generateTAId() {
        List<User> tas = getAllTAs();
        return tas.size() + 1;
    }
}
