package gr.cf9.pants.expense_tracker.dto.account_dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AccountUpdateDTO(

        @NotNull
        @Size(min = 3, max = 50)
        String name,

        @NotNull
        BigDecimal balance
) {}
