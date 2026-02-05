package model.exception;

public class DivisionByZeroException extends ExpressionEvaluationException {
    public DivisionByZeroException(String message) {
        super(message);
    }
}
