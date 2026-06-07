package gr.cf9.pants.expense_tracker.core.exceptions;

public class UnauthorizedException extends AppGenericException {
    private static final String DEFAULT_CODE = "Unauthorized";

    public UnauthorizedException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
