package gr.cf9.pants.expense_tracker.dto;

import jakarta.validation.constraints.NotNull;

public record AuthenticationRequestDTO(
        @NotNull
        String username,

        @NotNull
        String password
) {
}
