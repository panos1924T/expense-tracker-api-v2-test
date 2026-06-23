package gr.cf9.pants.expense_tracker.dto.transaction_dto;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionUpdateDTO(

        @NotNull
        LocalDate transactionDate,

        @NotNull @Positive
        BigDecimal amount,

        @Size(max = 255)
        String description,

        UUID sourceAccountUuid,

        UUID targetAccountUuid,

        UUID categoryUuid
) {}
