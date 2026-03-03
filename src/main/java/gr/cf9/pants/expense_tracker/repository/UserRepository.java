package gr.cf9.pants.expense_tracker.repository;

import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findBuUuid(UUID uuid);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);


}
