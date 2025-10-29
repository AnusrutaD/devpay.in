package in.devpay.user_service.services.interfaces;

import in.devpay.user_service.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    User createUser(User user);

    Optional<User> getUserById(UUID id);

    Optional<User> getUserByUsername(String username);

    Optional<User> getUserByEmail(String email);

    Optional<User> getUserByPhone(String phone);

    Optional<User> getUserByLogin(String login);

    List<User> getAllUsers();

    User updateUser(User user);

    void deleteUser(User user);

    boolean isUsernameExist(String username);

    boolean isEmailExist(String email);

    boolean isPhoneExist(String phone);
}
