package gr.cf9.pants.expense_tracker.repository;

import gr.cf9.pants.expense_tracker.model.Capability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CapabilityRepository extends JpaRepository<Capability, Long> {

    Optional<Capability> findCapabilityByName(String name);

    boolean existsCapabilityByName(String name);
}
