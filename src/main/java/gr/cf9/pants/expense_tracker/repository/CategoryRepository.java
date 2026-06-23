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

    List<Category> findCategoryByUserAndType(User user, TransactionType type);

    List<Category> findCategoryByUserAndDeletedFalse(User user);

    List<Category> findCategoryByUser(User user);

    Optional<Category> findCategoryByUuidAndUserAndDeletedFalse(UUID uuid, User user);

    Optional<Category> findCategoryByUuidAndUser(UUID Uuid, User user);

    Optional<Category> findCategoryByUuidAndUserAndTypeAndDeletedFalse(
            UUID uuid, User user, TransactionType transactionType
    );

    List<Category> findCategoryByUserAndParentIsNullAndDeletedFalse(User user);

    List<Category> findCategoryByUserAndTypeAndParentIsNullAndDeletedFalse(User user, TransactionType type);

    List<Category> findCategoryByUserAndParentAndDeletedFalse(User user, Category parent);

    List<Category> findCategoryByUserAndTypeAndParentAndDeletedFalse(
            User user,
            TransactionType type,
            Category parent
    );

    boolean existsCategoryByUserAndNameAndTypeAndParentIsNull(
            User user,
            String name,
            TransactionType type
    );

    boolean existsCategoryByUserAndNameAndTypeAndParent(
            User user,
            String name,
            TransactionType type,
            Category parent
    );

    boolean existsCategoryByParentAndDeletedFalse(Category parent);
}
