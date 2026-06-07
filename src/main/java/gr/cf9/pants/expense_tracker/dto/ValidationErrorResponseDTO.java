package gr.cf9.pants.expense_tracker.dto;

import java.util.Map;

public record ValidationErrorResponseDTO(
        String code,
        String message,
        Map<String, String> error
) {
}
