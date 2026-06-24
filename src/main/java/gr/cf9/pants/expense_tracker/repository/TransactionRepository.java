package gr.cf9.pants.expense_tracker.repository;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.model.Account;
import gr.cf9.pants.expense_tracker.model.Category;
import gr.cf9.pants.expense_tracker.model.Transaction;
import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findTransByUser(User user, Pageable pageable);

    @Query("SELECT t FROM Transaction t " +
            "WHERE t.user = :user " +
            "AND (t.sourceAccount = :account OR t.targetAccount = :account)")
    Page<Transaction> findTransByUserAndAccount(
            @Param("user") User user,
            @Param("account") Account account,
            Pageable pageable
    );

    Page<Transaction> findTransByUserAndType(User user, TransactionType type, Pageable pageable);

    Page<Transaction> findTransByUserAndCategory(User user, Category category, Pageable pageable);


    @Query("SELECT t FROM Transaction t " +
            "WHERE t.user = :user " +
            "AND t.category.parent = :parent")
    Page<Transaction> findTransByUserAndCategory_Parent(
            @Param("user") User user,
            @Param("parent") Category parent,
            Pageable pageable
    );

    @Query("""
    SELECT t FROM Transaction t
    WHERE t.user = :user
      AND (t.category = :category OR t.category.parent = :category)
    ORDER BY t.transactionDate DESC
    """)
    Page<Transaction> findTransByUserAndCategoryOrChildCategory(
            User user, Category category, Pageable pageable
    );

    Optional<Transaction> findTransByUuidAndUser(UUID uuid, User user);
}
