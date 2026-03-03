package gr.cf9.pants.expense_tracker.dto.transactionDTO;

import gr.cf9.pants.expense_tracker.enums.TransactionType;

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
