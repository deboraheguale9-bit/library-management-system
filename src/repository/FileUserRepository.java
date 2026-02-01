package repository;

import model.User;
import model.UserRole;
import java.io.*;
import java.util.*;

public class FileUserRepository implements UserRepository {
    private List<User> users;
    private String filePath;

    public FileUserRepository(String filePath) {
        this.filePath = filePath;
        this.users = new ArrayList<>();
        loadFromFile();

        if (users.isEmpty()) {
            createDefaultUsers();
        }
    }

    @Override
    public void save(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                saveToFile();
                return;
            }
        }
        users.add(user);
        saveToFile();
    }

    @Override
    public User findById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return users.stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public boolean delete(String id) {
        boolean removed = users.removeIf(user -> user.getId().equals(id));
        if (removed) {
            saveToFile();
        }
        return removed;
    }

    @Override
    public boolean update(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                saveToFile();
                return true;
            }
        }
        return false;
    }

    private void createDefaultUsers() {
        // SIMPLE hardcoded users (no reflection needed)
        users.add(new User("admin001", "System Administrator", "admin@library.com",
                "555-0000", "admin", "admin123", UserRole.ADMIN) {});
        users.add(new User("lib001", "Librarian User", "librarian@library.com",
                "555-0001", "librarian", "lib123", UserRole.LIBRARIAN) {});
        users.add(new User("mem001", "Member User", "member@library.com",
                "555-0002", "member", "mem123", UserRole.MEMBER) {});

        saveToFile();
        System.out.println("✅ Created default users in file");
    }

    private void loadFromFile() {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("⚠️ File doesn't exist, will create: " + filePath);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User user = parseUser(line);
                if (user != null) {
                    users.add(user);
                }
            }
            System.out.println("✅ Loaded " + users.size() + " users from: " + filePath);
        } catch (IOException e) {
            System.err.println("❌ Error loading file: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (User user : users) {
                writer.println(serializeUser(user));
            }
            System.out.println("✅ Saved " + users.size() + " users to: " + filePath);
        } catch (IOException e) {
            System.err.println("❌ Error saving file: " + e.getMessage());
        }
    }

    private String serializeUser(User user) {
        return String.format("%s,%s,%s,%s,%s,%s,%s",
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getMobile(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getRole().toString()
        );
    }

    private User parseUser(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length == 7) {
                return new User(
                        parts[0], // id
                        parts[1], // name
                        parts[2], // email
                        parts[3], // mobile
                        parts[4], // username
                        parts[5], // password
                        UserRole.valueOf(parts[6]) // role
                ) {};
            }
        } catch (Exception e) {
            System.err.println("❌ Error parsing user line: " + line);
        }
        return null;
    }
}