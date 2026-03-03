package gr.cf9.pants.expense_tracker.dto.transaction_dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransferCreateDTO(
    Long sourceAccountId,
    Long targetAccountId,
    BigDecimal amount,
    LocalDate transactionDate,
    String description
) {}
