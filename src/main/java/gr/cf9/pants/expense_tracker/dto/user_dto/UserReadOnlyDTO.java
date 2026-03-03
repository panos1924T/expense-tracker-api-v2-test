package gr.cf9.pants.expense_tracker.dto.user_dto;

import java.time.Instant;
import java.util.UUID;

public record UserReadOnlyDTO(
        UUID uuid,
        String username,
        String email,
        Instant createdAt
) {}
