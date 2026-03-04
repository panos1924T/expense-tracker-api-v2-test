package gr.cf9.pants.expense_tracker.dto.transaction_dto;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionCreateDTO(
   Long sourceAccountId,
   Long categoryId,
   TransactionType type,
   BigDecimal amount,
   LocalDate transactionDate,
   String description
) {}
