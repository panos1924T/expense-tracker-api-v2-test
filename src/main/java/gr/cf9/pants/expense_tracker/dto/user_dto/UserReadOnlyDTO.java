package gr.cf9.pants.expense_tracker.dto.user_dto;

import java.util.UUID;

public record UserReadOnlyDTO(
        UUID uuid,
        String email,
        String username
) {}
