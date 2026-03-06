package gr.cf9.pants.expense_tracker.core.exceptions;

public class ValidationException extends AppGenericException{

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
}
