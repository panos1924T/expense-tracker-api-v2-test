package gr.cf9.pants.expense_tracker.dto.category_dto;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;

import java.util.UUID;

public record CategoryCreateDTO(
        String name,
        TransactionType type,
        UUID parentUuid
) {}
