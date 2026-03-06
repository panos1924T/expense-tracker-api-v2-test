package gr.cf9.pants.expense_tracker.core.exceptions;

public class EntityAlreadyExistsException extends AppGenericException {

    public EntityAlreadyExistsException(String message) {
        super("ALREADY_EXISTS", message);
    }
}
