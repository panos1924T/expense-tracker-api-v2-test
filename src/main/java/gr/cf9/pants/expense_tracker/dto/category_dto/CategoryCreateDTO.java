package gr.cf9.pants.expense_tracker.dto.category_dto;

import gr.cf9.pants.expense_tracker.core.enums.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CategoryCreateDTO(

        @NotNull
        @Size(min = 3, max = 50)
        String name,

        @NotNull
        TransactionType type,

        UUID parentUuid
) {}
