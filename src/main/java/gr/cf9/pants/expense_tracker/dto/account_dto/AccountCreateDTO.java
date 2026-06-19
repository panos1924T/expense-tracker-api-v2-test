package gr.cf9.pants.expense_tracker.dto.account_dto;

import gr.cf9.pants.expense_tracker.core.enums.AccountType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record AccountCreateDTO(

   @NotNull
   @Size(min = 3, max = 50)
   String name,

   @NotNull
   AccountType accountType,

   BigDecimal initialBalance
) {

    public AccountCreateDTO {
        if (initialBalance == null) {
            initialBalance = BigDecimal.ZERO;
        }
    }

}
