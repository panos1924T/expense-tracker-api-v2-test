package gr.cf9.pants.expense_tracker.core.exceptions;

import lombok.Getter;

@Getter
public class InvalidTransactionException extends AppGenericException {
    private static final String DEFAULT_CODE = "InvalidTransaction";

    public InvalidTransactionException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
