package gr.cf9.pants.expense_tracker.repository;

import gr.cf9.pants.expense_tracker.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findRoleByName(String name);

    boolean existsRoleByName(String name);

    List<Role> findAllByOrderByNameAsc();
}
