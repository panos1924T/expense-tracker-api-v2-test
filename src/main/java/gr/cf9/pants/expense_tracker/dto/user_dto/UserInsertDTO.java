package gr.cf9.pants.expense_tracker.dto.user_dto;

public record UserInsertDTO(
        String email,
        String username,
        String password) {}
