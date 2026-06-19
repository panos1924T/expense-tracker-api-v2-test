package gr.cf9.pants.expense_tracker.dto.category_dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryUpdateDTO(

        @NotNull
        @Size(min = 3, max = 50)
        String name
) {}
