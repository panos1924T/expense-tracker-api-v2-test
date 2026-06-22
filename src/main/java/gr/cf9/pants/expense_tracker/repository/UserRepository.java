package gr.cf9.pants.expense_tracker.repository;

import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"role", "role.capabilities"})
    Optional<User> findUserByEmail(String email);

    @EntityGraph(attributePaths = {"role", "role.capabilities"})
    Optional<User> findUserByEmailAndDeletedFalse(String email);

    @EntityGraph(attributePaths = {"role", "role.capabilities"})
    Optional<User> findUserByUuid(UUID uuid);

    @EntityGraph(attributePaths = {"role", "role.capabilities"})
    Optional<User> findUserByUuidAndDeletedFalse(UUID uuid);

    Page<User> findAllUsersByDeletedFalse(Pageable pageable);

    boolean existsUserByEmail(String email);

    @EntityGraph(attributePaths = {"role", "role.capabilities"})
    boolean existsUserByEmailAndUuidNot(String email, UUID uuid);

}
