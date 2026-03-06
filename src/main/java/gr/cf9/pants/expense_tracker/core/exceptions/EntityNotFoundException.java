package gr.cf9.pants.expense_tracker.core.exceptions;

public class EntityNotFoundException extends AppGenericException {
    public EntityNotFoundException(String message) {
        super("NOT_FOUND", message);
    }
}
