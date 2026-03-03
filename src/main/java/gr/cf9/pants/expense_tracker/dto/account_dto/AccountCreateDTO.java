package gr.cf9.pants.expense_tracker.dto.account_dto;

import java.math.BigDecimal;

public record AccountCreateDTO(
   String name,
   String accountType,
   BigDecimal initialBalance
) {}
