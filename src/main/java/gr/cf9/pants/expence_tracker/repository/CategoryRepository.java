package gr.cf9.pants.expence_tracker.repository;

import gr.cf9.pants.expence_tracker.enums.TransactionType;
import gr.cf9.pants.expence_tracker.model.Category;
import gr.cf9.pants.expence_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByUserAndCategory(User user, TransactionType type);

    List<Category> findByUser(User user);

    Optional<Category> findByIdAndUser(Long id, User user);
}
