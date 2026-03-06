package gr.cf9.pants.expense_tracker.core.exceptions;

public class UnauthorizedException extends AppGenericException {
    public UnauthorizedException(String message) {
        super("UNAUTHORIZED", message);
    }
}
