package gr.cf9.pants.expense_tracker.core.exceptions;

import lombok.Getter;

@Getter
public class InsufficientBalanceException extends Exception {
    private final String code = "INSUFFICIENT_BALANCE";

    public InsufficientBalanceException(String message) {
        super(message);
    }
}
