package gr.cf9.pants.expense_tracker.repository;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.model.Category;
import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findCategoryByUserAndType(User user, TransactionType type);

    List<Category> findCategoryByUser(User user);

    Optional<Category> findCategoryByUuidAndUser(UUID uuid, User user);

    Optional<Category> findCategoryByUuid(UUID categoryUuid)
}
