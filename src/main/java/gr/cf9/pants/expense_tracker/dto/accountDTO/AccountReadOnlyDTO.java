package gr.cf9.pants.expense_tracker.dto.accountDTO;

import java.math.BigDecimal;
import java.time.Instant;

public record AccountReadOnlyDTO(
    Long id,
    String name,
    String accountType,
    BigDecimal balance,
    Instant createdAt
) {}
