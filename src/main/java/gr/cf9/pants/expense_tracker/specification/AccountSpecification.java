package gr.cf9.pants.expense_tracker.specification;

import gr.cf9.pants.expense_tracker.core.enums.AccountType;
import gr.cf9.pants.expense_tracker.core.filters.AccountFilters;
import gr.cf9.pants.expense_tracker.model.Account;
import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.data.jpa.domain.Specification;

public class AccountSpecification {

    public static Specification<Account> build(AccountFilters filters, User user) {
        return Specification.allOf(
                belongsToUser(user),
                isDeleted(filters.isIncludeDeleted()),
                hasNameLike(filters.getName()),
                hasAccountType(filters.getAccountType()),
                isDefaultAccount(filters.getDefaultAccount())
        );
    }

    private static Specification<Account> belongsToUser(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }

    private static Specification<Account> isDeleted(boolean includeDeleted) {
        return (root, query, cb) -> includeDeleted ? cb.conjunction() : cb.equal(root.get("deleted"), false);
    }

    private static Specification<Account> hasNameLike(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
        };
    }

    private static Specification<Account> hasAccountType(AccountType accountType) {
        return (root, query, cb) -> accountType == null ? cb.conjunction() : cb.equal(root.get("accountType"), accountType);
    }

    private static Specification<Account> isDefaultAccount(Boolean defaultAccount) {
        return (root, query, cb) -> defaultAccount == null ? cb.conjunction() : cb.equal(root.get("defaultAccount"), defaultAccount);
    }
}