package gr.cf9.pants.expense_tracker.core.exceptions;

public class InvalidTransactionException extends AppGenericException {
    public InvalidTransactionException(String message) {
        super("INVALID_TRANSACTION", message);
    }
}
