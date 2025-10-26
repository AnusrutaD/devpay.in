package in.devpay.user_service.repositories;

import in.devpay.user_service.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmail(String email);

    default Optional<User> findByLogin(String login) {
        return findByUsername(login)
                .or(() -> findByPhone(login))
                .or(() -> findByEmail(login));
    }
}
