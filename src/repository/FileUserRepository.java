package repository;

import model.User;
import model.UserRole;
import java.util.ArrayList;
import java.util.List;

public class FileUserRepository implements UserRepository {
    private List<User> users;
    private String filePath;

    public FileUserRepository(String filePath) {
        this.filePath = filePath;
        this.users = new ArrayList<>();
        loadFromFile();

        // Add default admin if no users exist
        if (users.isEmpty()) {
            createDefaultAdmin();
        }
    }

    @Override
    public void save(User user) {
        // Check if user already exists
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user); // Update existing
                saveToFile();
                return;
            }
        }
        // Add new user
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

    private void createDefaultAdmin() {
        // Create a default admin user (password: admin123)
        try {
            Class<?> adminClass = Class.forName("model.Admin");
            User defaultAdmin = (User) adminClass.getConstructor(
                    String.class, String.class, String.class, String.class,
                    String.class, String.class, int.class
            ).newInstance(
                    "admin001", "System Administrator", "admin@library.com",
                    "555-0000", "admin", "admin123", 1
            );
            users.add(defaultAdmin);
            saveToFile();
            System.out.println("✅ Created default admin user");
        } catch (Exception e) {
            System.out.println("⚠️ Could not create default admin: " + e.getMessage());
        }
    }

    private void loadFromFile() {
        System.out.println("Loading users from: " + filePath);
        // TODO: Implement CSV/JSON reading
    }

    private void saveToFile() {
        System.out.println("Saving users to: " + filePath);
        // TODO: Implement CSV/JSON writing
    }
}
