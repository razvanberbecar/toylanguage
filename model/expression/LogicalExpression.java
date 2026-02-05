package model.expression;

import model.exception.ExpressionEvaluationException;
import model.exception.MyException;
import model.state.Heap;
import model.state.SymbolTable;
import model.state.TypeEnvironment;
import model.type.BoolType;
import model.type.Type;
import model.value.BooleanValue;
import model.value.Value;

public record LogicalExpression
        (String operator, Expression left, Expression right)
        implements Expression {

    @Override
    public Value evaluate(SymbolTable symTable, Heap hp) throws MyException {
        var leftTerm = left.evaluate(symTable, hp);
        var rightTerm = right.evaluate(symTable, hp);

        switch (operator) {
            case "&&", "||":
                checkTypes(leftTerm, rightTerm, new BoolType());
                var leftValueB = (BooleanValue) leftTerm;
                var rightValueB = (BooleanValue) rightTerm;
                return evaluateBooleanExpression(leftValueB, rightValueB);
        }

        throw new ExpressionEvaluationException("Unknown operator" + operator);
    }

    @Override
    public Type typecheck(TypeEnvironment typeEnv) throws MyException {
        var t1 = left.typecheck(typeEnv);
        var t2 = right.typecheck(typeEnv);
        if (t1.equals(new BoolType())) {
            if (t2.equals(new BoolType())) return new BoolType();
            else throw new MyException("second operand is not a boolean");
        } else throw new MyException("first operand is not a boolean");
    }

    private void checkTypes(Value leftTerm, Value rightTerm, Type type) throws MyException{
        if (leftTerm.getType() != type ||
                rightTerm.getType() != type) {
            throw new ExpressionEvaluationException("Wrong types for operator " + operator);
        }
    }


    private BooleanValue evaluateBooleanExpression(BooleanValue leftValue, BooleanValue rightValue) {
        return switch (operator) {
            case "&&" -> new BooleanValue(leftValue.value() && rightValue.value());
            case "||" -> new BooleanValue(leftValue.value() || rightValue.value());
            default -> throw new IllegalStateException("Unreachable code");
        };
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }
}
