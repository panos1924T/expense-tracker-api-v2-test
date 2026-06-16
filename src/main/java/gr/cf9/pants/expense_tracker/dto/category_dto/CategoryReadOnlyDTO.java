package gr.cf9.pants.expense_tracker.dto.category_dto;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;

import java.util.UUID;

public record CategoryReadOnlyDTO(
        UUID uuid,
        String name,
        TransactionType type,
        UUID parentUuid,
        String parentName,
        boolean group,
        boolean isDeleted
) {}
