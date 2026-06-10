package gr.cf9.pants.expense_tracker.dto.user_dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserInsertDTO(

        @Email
        @NotNull
        String email,

        @NotNull
        String username,

        @NotNull
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])^.{8,}$")
        String password) {}
