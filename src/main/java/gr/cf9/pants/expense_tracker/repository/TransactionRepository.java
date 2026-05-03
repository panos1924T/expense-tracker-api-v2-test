package gr.cf9.pants.expense_tracker.repository;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.model.Account;
import gr.cf9.pants.expense_tracker.model.Transaction;
import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findTransByUser(User user, Pageable pageable);

    Page<Transaction> findTransByUserAndSourceAccount(User user, Account account, Pageable pageable);

    Page<Transaction> findTransByUserAndType(User user, TransactionType type, Pageable pageable);

    Optional<Transaction> findTransByUuidAndUser(UUID uuid, User user);

    boolean existsTransByAccount(Account account);
}
