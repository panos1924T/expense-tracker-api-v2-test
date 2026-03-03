package gr.cf9.pants.expense_tracker.dto.userDTO;

import java.util.UUID;

public record UserReadOnlyDTO(
        UUID uuid,
        String username,
        String email,
        String createdAt
) {}
