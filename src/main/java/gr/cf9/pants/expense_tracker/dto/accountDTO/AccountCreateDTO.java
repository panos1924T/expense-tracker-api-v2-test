package gr.cf9.pants.expense_tracker.dto.accountDTO;

import java.math.BigDecimal;

public record AccountCreateDTO(
   String name,
   String accountType,
   BigDecimal initialBalance
) {}
