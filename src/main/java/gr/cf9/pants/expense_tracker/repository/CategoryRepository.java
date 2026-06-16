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

    List<Category> findCategoryByUserAndTypeAndDeletedFalse(User user, TransactionType type);

    List<Category> findCategoryByUserAndDeletedFalse(User user);

    Optional<Category> findCategoryByUuidAndUserAndDeletedFalse(UUID uuid, User user);

    List<Category> findCategoryByUserAndParentIsNullAndDeletedFalse(User user);

    List<Category> findCategoryByUserAndTypeAndParentIsNullAndDeletedFalse(User user, TransactionType type);

    List<Category> findCategoryByUserAndParentAndDeletedFalse(User user, Category parent);

    List<Category> findCategoryByUserAndTypeAndParentAndDeletedFalse(
            User user,
            TransactionType type,
            Category parent
    );

    boolean existsCategoryByUserAndNameAndTypeAndParentIsNullAndDeletedFalse(
            User user,
            TransactionType type,
            String name
    );

    boolean existsCategoryByUserAndNameAndTypeAndParentAndDeletedFalse(
            User user,
            TransactionType type,
            String name,
            Category parent
    );

    boolean existsCategoryByParentAndDeletedFalse(Category parent);

    //all categories for history or filtering
    Optional<Category> findCategoryByUuidAndUser(UUID uuid, User user);
}
