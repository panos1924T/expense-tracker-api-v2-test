package gr.cf9.pants.expense_tracker.core.exceptions;

public class InsufficientBalanceException extends AppGenericException {
    public InsufficientBalanceException(String message) {
        super("INSUFFICIENT_BALANCE", message);
    }
}
