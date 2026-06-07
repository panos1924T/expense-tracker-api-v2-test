package gr.cf9.pants.expense_tracker.dto;

public record ErrorResponseDTO(
        String code,
        String message
) {
    public ErrorResponseDTO(String code) {
        this(code, "");
    }
}
