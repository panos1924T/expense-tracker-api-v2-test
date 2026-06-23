package gr.cf9.pants.expense_tracker.dto.account_dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AccountReadOnlyDTO(
    UUID uuid,
    String name,
    String accountType,
    BigDecimal balance,
    Instant createdAt
) {}
