package gr.cf9.pants.expense_tracker.dto.transactionDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferRequestDTO (
    Long sourceAccountId,
    Long targetAccountId,
    BigDecimal amount,
    LocalDate transactionDate,
    String description
) {}
