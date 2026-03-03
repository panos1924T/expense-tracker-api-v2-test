package gr.cf9.pants.expence_tracker.repository;

import gr.cf9.pants.expence_tracker.enums.TransactionType;
import gr.cf9.pants.expence_tracker.model.Account;
import gr.cf9.pants.expence_tracker.model.Transaction;
import gr.cf9.pants.expence_tracker.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByUser(User user, Pageable pageable);

    Page<Transaction> findByUserAndSourceAccount(User user, Account account, Pageable pageable);

    Page<Transaction> findByUserAndType(User user, TransactionType type, Pageable pageable);

}
