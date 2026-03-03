package gr.cf9.pants.expense_tracker.dto.transaction_dto;

import gr.cf9.pants.expense_tracker.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record TransactionReadOnlyDTO(
        Long id,
        BigDecimal amount,
        LocalDate transactionDate,
        String description,
        TransactionType type,
        Long sourceAccountId,
        String sourceAccountName,
        Long targetAccountId,
        String targetAccountName,
        Long categoryId,
        String categoryName,
        Instant createdAt,
        Instant updatedAt
) {}