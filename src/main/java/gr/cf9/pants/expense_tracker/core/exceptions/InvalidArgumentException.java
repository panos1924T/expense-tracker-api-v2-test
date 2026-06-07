package gr.cf9.pants.expense_tracker.core.exceptions;

public class InvalidArgumentException extends AppGenericException {
    private static final String DEFAULT_CODE = "InvalidArgument";

    public InvalidArgumentException(String code, String message) {
        super(code + DEFAULT_CODE, message);
    }
}
