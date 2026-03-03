package gr.cf9.pants.expense_tracker.dto.userDTO;

public record UserRegisterDTO(
        String email,
        String username,
        String password) {}
