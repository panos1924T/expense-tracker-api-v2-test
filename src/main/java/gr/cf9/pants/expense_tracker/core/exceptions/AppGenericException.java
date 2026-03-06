package gr.cf9.pants.expense_tracker.core.exceptions;

public class AppGenericException extends RuntimeException {
    private final String code;

    public AppGenericException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCODE() {
        return code;
    }
}
