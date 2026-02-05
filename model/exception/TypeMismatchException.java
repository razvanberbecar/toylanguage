package model.exception;

public class TypeMismatchException extends ExpressionEvaluationException {
    public TypeMismatchException(String message) {
        super(message);
    }
}
