package notebook.controller;

import notebook.model.User;
import notebook.model.repository.GBRepository;
import notebook.util.UserDialogue;

import java.util.List;
import java.util.Objects;

public class UserController {
    private final GBRepository repository;

    public UserController(GBRepository repository) {
        this.repository = repository;
    }

    public void saveUser(User user) {
        repository.create(user);
    }

    public User readUser(Long userId) throws Exception {
        return repository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public List<User> readAllUsers() {
        return repository.findAll();
    }

    public User createUser() {
        String firstName = UserDialogue.prompt("Имя: ");
        String lastName = UserDialogue.prompt("Фамилия: ");
        String phone = UserDialogue.prompt("Номер телефона: ");
        return new User(firstName, lastName, phone);
    }

    public void updateUser(String userId, User update) {
        update.setId(Long.parseLong(userId));
        repository.update(Long.parseLong(userId), update);
    }

    public void deleteUser(String userId) {
        repository.delete(Long.parseLong(userId));
    }
}
