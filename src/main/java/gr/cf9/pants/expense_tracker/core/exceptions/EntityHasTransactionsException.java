package gr.cf9.pants.expense_tracker.core.exceptions;

public class EntityHasTransactionsException extends AppGenericException {
    private static final String DEFAULT_CODE = "HasTransactions";

    public EntityHasTransactionsException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
