package gr.cf9.pants.expense_tracker.core.exceptions;

import lombok.Getter;

@Getter
public class InvalidTransactionException extends Exception {
    private final String code = "INVALID_TRANSACTION";

    public InvalidTransactionException(String message) {
        super(message);
    }
}
