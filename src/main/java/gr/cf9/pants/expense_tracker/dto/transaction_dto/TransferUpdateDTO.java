package gr.cf9.pants.expense_tracker.dto.transaction_dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransferUpdateDTO(
        @NotNull @Positive
        BigDecimal amount,

        @NotNull
        LocalDate transactionDate,

        @Size(max = 255)
        String description,

        @NotNull
        UUID sourceAccountUuid,

        @NotNull
        UUID targetAccountUuid
) {
}
