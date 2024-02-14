package notebook.model.repository;

import notebook.model.User;

import java.util.List;
import java.util.Optional;

public interface GBRepository {
    List<String> readAll();
    void saveAll(List<String> data);
    List<User> findAll();
    User create(User user);
    Optional<User> findById(Long id);
    Optional<User> update(Long userId, User update);
    void delete(Long id);
}
