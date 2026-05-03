package gr.cf9.pants.expense_tracker.dto.transaction_dto;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionCreateDTO(
   UUID sourceAccountUuid,
   UUID categoryUuid,
   TransactionType type,
   BigDecimal amount,
   LocalDate transactionDate,
   String description
) {}
