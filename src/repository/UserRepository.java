package repository;

import model.User;
import java.util.List;

public interface UserRepository {
    void save(User user);
    User findById(String id);
    User findByUsername(String username);
    User findByEmail(String email);
    List<User> findAll();
    boolean delete(String id);
    boolean update(User user);
}