package gr.cf9.pants.expense_tracker.specification;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import gr.cf9.pants.expense_tracker.core.filters.CategoryFilters;
import gr.cf9.pants.expense_tracker.model.Category;
import gr.cf9.pants.expense_tracker.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class CategorySpecification {

    public static Specification<Category> build(CategoryFilters filters, User user) {
        return Specification.allOf(
                belongsToUser(user),
                isDeleted(filters.isIncludeDeleted()),
                hasNameLike(filters.getName()),
                hasType(filters.getType()),
                isParentCategory(filters.getIsParent()),
                hasParentUuid(filters.getParentUuid())
        );
    }

    private static Specification<Category> belongsToUser(User user) {
        return (root, query, cb) -> cb.equal(root.get("user"), user);
    }

    private static Specification<Category> isDeleted(boolean includeDeleted) {
        return (root, query, cb) -> includeDeleted ? cb.conjunction() : cb.equal(root.get("deleted"), false);
    }

    private static Specification<Category> hasNameLike(String name) {
        return (root, query, cb) -> {
            if (name == null || name.trim().isBlank()) return cb.conjunction();
            return cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
        };
    }

    private static Specification<Category> hasType(TransactionType type) {
        return (root, query, cb) -> type == null ? cb.conjunction() : cb.equal(root.get("type"), type);
    }

    private static Specification<Category> isParentCategory(Boolean isParent) {
        return (root, query, cb) -> {
            if (isParent == null) return cb.conjunction();
            return isParent ? cb.isNull(root.get("parent")) : cb.isNotNull(root.get("parent"));
        };
    }

    private static Specification<Category> hasParentUuid(UUID parentUuid) {
        return (root, query, cb) -> {
            if (parentUuid == null) return cb.conjunction();

            var parentJoin = root.join("parent", jakarta.persistence.criteria.JoinType.LEFT);
            return cb.equal(parentJoin.get("uuid"), parentUuid);
        };
    }
}