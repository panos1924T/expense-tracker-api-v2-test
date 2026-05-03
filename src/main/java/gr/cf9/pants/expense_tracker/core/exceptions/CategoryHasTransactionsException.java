package gr.cf9.pants.expense_tracker.core.exceptions;

public class CategoryHasTransactionsException extends AppGenericException {
    public CategoryHasTransactionsException(String message) {
        super("CATEGORY_INVALID" ,message);
    }
}
