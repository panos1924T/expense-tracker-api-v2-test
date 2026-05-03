package gr.cf9.pants.expense_tracker.repository;

import gr.cf9.pants.expense_tracker.model.Account;
import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAccountByUser(User user);

    Optional<Account> findAccountByUuidAndUser(UUID uuid, User user);
}
