package gr.cf9.pants.expense_tracker.dto.categoryDTO;

import gr.cf9.pants.expense_tracker.enums.TransactionType;

public record CategoryReadOnlyDTO(
        Long id,
        String name,
        TransactionType type,
        Long parentId,
        String parentName,
        boolean isSystemDefault
) {}
