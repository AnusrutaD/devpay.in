package in.devpay.user_service.services;

import in.devpay.user_service.entities.User;
import in.devpay.user_service.repositories.UserRepository;
import in.devpay.user_service.services.interfaces.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private UserRepository repository;

    @Override
    public User createUser(User user) {
        User createdUser = repository.save(user);
        log.info("User created successfully with Id: " + createdUser.getId());
        return createdUser;
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User updateUser(User user) {
        User dbUser = this.getUserById(user.getId()).get();
        dbUser = user;
        return repository.save(dbUser);
    }

    @Override
    public void deleteUser(User user) {
        repository.delete(user);
    }
}
