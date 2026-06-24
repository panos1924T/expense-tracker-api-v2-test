package gr.cf9.pants.expense_tracker.specification;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.core.filters.TransactionFilters;
import gr.cf9.pants.expense_tracker.model.Transaction;
import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class TransactionSpecification {

    public static Specification<Transaction> build(TransactionFilters filters, User user) {
        return Specification.allOf(
                belongsToUser(user),
                hasType(filters.getType()),
                hasCategory(filters.getCategoryUuid()),
                involvesAccount(filters.getAccountUuid()),
                isBetweenDates(filters.getStartDate(), filters.getEndDate()),
                isAmountBetween(filters.getMinAmount(), filters.getMaxAmount())
        );
    }

    private static Specification<Transaction> belongsToUser(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }

    private static Specification<Transaction> isDeleted(boolean includeDeleted) {
        return (root, query, cb) -> includeDeleted ? cb.conjunction() : cb.equal(root.get("deleted"), false);
    }

    private static Specification<Transaction> hasType(TransactionType type) {
        return (root, query, cb) -> type == null ? cb.conjunction() : cb.equal(root.get("type"), type);
    }

    private static Specification<Transaction> hasCategory(UUID categoryUuid) {
        return (root, query, cb) -> categoryUuid == null ? cb.conjunction() :
                cb.or(
                        cb.equal(root.get("category").get("uuid"), categoryUuid),
                        cb.equal(root.get("category").get("parent").get("uuid"), categoryUuid)
                );
    }

    private static Specification<Transaction> involvesAccount(UUID accountUuid) {
        return (root, query, cb) -> accountUuid == null ? cb.conjunction() :
                cb.or(
                        cb.equal(root.get("sourceAccount").get("uuid"), accountUuid),
                        cb.equal(root.get("targetAccount").get("uuid"), accountUuid)
                );
    }

    private static Specification<Transaction> isBetweenDates(LocalDate start, LocalDate end) {
        return (root, query, cb) -> {
            if (start != null && end != null) return cb.between(root.get("transactionDate"), start, end);
            if (start != null) return cb.greaterThanOrEqualTo(root.get("transactionDate"), start);
            if (end != null) return cb.lessThanOrEqualTo(root.get("transactionDate"), end);
            return cb.conjunction();
        };
    }

    private static Specification<Transaction> isAmountBetween(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min != null && max != null) return cb.between(root.get("amount"), min, max);
            if (min != null) return cb.greaterThanOrEqualTo(root.get("amount"), min);
            if (max != null) return cb.lessThanOrEqualTo(root.get("amount"), max);
            return cb.conjunction();
        };
    }
}