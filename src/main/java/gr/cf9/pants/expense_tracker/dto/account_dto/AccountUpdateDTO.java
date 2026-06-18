package gr.cf9.pants.expense_tracker.dto.account_dto;

import java.math.BigDecimal;

public record AccountUpdateDTO(
   String name,
   BigDecimal balance
) {}
