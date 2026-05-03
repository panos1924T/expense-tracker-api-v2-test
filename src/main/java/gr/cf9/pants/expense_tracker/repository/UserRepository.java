package gr.cf9.pants.expense_tracker.repository;

import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByUuid(UUID uuid);

    boolean existsUserByEmail(String email);
    boolean existsUserByEmailAndUuidNot(String email, UUID uuid);

}
