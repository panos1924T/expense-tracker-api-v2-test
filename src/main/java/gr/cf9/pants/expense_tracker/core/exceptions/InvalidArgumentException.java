package gr.cf9.pants.expense_tracker.core.exceptions;

public class InvalidArgumentException extends AppGenericException {
    public InvalidArgumentException(String message) {
        super("INVALID_ARGUMENT", message);
    }
}
