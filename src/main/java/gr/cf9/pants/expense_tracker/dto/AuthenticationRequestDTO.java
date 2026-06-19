package gr.cf9.pants.expense_tracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequestDTO(
        @NotNull
        @Email
        String email,

        @NotNull
        String password
) {
}
