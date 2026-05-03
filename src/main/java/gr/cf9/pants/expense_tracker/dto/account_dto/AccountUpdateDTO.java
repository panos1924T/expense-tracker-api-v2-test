package gr.cf9.pants.expense_tracker.dto.account_dto;

import gr.cf9.pants.expense_tracker.core.enums.AccountType;

import java.math.BigDecimal;

public record AccountUpdateDTO(
   String name,
   AccountType accountType,
   BigDecimal balance
) {}
