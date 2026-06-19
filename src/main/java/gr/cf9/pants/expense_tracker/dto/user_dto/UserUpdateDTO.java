package gr.cf9.pants.expense_tracker.dto.user_dto;

import jakarta.validation.constraints.*;

public record UserUpdateDTO(

        @NotNull
        @Size(min = 3, max = 21)
        String username,

        @NotNull
        @Email
        String email,

        @NotNull
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=])^.{8,}$")
        String password
){}
