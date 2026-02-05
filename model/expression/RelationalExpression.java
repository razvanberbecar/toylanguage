package model.expression;

import model.exception.ExpressionEvaluationException;
import model.exception.MyException;
import model.state.Heap;
import model.state.SymbolTable;
import model.state.TypeEnvironment;
import model.type.BoolType;
import model.type.IntType;
import model.type.Type;
import model.value.BooleanValue;
import model.value.IntegerValue;
import model.value.Value;

public record RelationalExpression(String operator, Expression left, Expression right) implements Expression {

    @Override
    public Value evaluate(SymbolTable symTable, Heap hp) throws MyException {
        Value leftValue = left.evaluate(symTable, hp);
        Value rightValue = right.evaluate(symTable, hp);

        if (!leftValue.getType().equals(new IntType()) || !rightValue.getType().equals(new IntType())) {
            throw new ExpressionEvaluationException("Relational operators require integer operands");
        }

        IntegerValue leftInt = (IntegerValue) leftValue;
        IntegerValue rightInt = (IntegerValue) rightValue;

        return evaluateRelation(leftInt.value(), rightInt.value());
    }

    @Override
    public Type typecheck(TypeEnvironment typeEnv) throws MyException {
        Type t1 = left.typecheck(typeEnv);
        Type t2 = right.typecheck(typeEnv);
        if (t1.equals(new IntType())) {
            if (t2.equals(new IntType())) return new BoolType();
            else throw new MyException("second operand is not an integer");
        } else throw new MyException("first operand is not an integer");
    }

    private BooleanValue evaluateRelation(int left, int right) throws MyException {
        return switch (operator) {
            case "<" -> new BooleanValue(left < right);
            case "<=" -> new BooleanValue(left <= right);
            case "==" -> new BooleanValue(left == right);
            case "!=" -> new BooleanValue(left != right);
            case ">" -> new BooleanValue(left > right);
            case ">=" -> new BooleanValue(left >= right);
            default -> throw new ExpressionEvaluationException("Unknown relational operator: " + operator);
        };
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }
}