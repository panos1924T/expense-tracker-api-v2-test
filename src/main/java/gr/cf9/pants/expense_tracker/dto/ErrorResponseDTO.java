package gr.cf9.pants.expense_tracker.dto;

public record ErrorResponseDTO(
        String code,
        String message
) {}
