package gr.cf9.pants.expence_tracker.repository;

import gr.cf9.pants.expence_tracker.model.Account;
import gr.cf9.pants.expence_tracker.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository {

    List<Account> findByUser(User user);

    Optional<Account> findByIdAndUser(Long id, User user);
}
