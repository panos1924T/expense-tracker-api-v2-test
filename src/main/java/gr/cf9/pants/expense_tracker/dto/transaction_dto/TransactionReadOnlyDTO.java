package gr.cf9.pants.expense_tracker.dto.transaction_dto;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionReadOnlyDTO(
        UUID uuid,
        BigDecimal amount,
        LocalDate transactionDate,
        String description,
        TransactionType type,
        UUID sourceAccountUuid,
        String sourceAccountName,
        UUID targetAccountUuid,
        String targetAccountName,
        UUID categoryUuid,
        String categoryName,
        Instant createdAt,
        Instant updatedAt
) {}