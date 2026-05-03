package gr.cf9.pants.expense_tracker.core.exceptions;

public class EntityHasTransactionsException extends AppGenericException {
    public EntityHasTransactionsException(String message) {
        super("CATEGORY_INVALID" ,message);
    }
}
