package gr.cf9.pants.expense_tracker.dto.transaction_dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransferCreateDTO(
    UUID sourceAccountUuid,
    UUID targetAccountUuid,
    BigDecimal amount,
    LocalDate transactionDate,
    String description
) {}
