package model.expression;

import model.exception.DivisionByZeroException;
import model.exception.ExpressionEvaluationException;
import model.exception.MyException;
import model.state.Heap;
import model.state.SymbolTable;
import model.state.TypeEnvironment;
import model.type.BoolType;
import model.type.IntType;
import model.type.Type;
import model.value.Value;
import model.value.IntegerValue;

public record ArithmeticalExpression(String operator, Expression left, Expression right) implements Expression {


    @Override
    public Value evaluate(SymbolTable symTable, Heap hp) throws MyException {
        var leftTerm = left.evaluate(symTable, hp);
        var rightTerm = right.evaluate(symTable, hp);
        checkTypes(leftTerm,rightTerm, new IntType());
        var leftValue=(IntegerValue) leftTerm;
        var rightValue=(IntegerValue) rightTerm;
        return evaluateArithExpr(leftValue,rightValue,operator);
    }

    @Override
    public Type typecheck(TypeEnvironment typeEnv) throws MyException {
        var t1 = left.typecheck(typeEnv);
        var t2 = right.typecheck(typeEnv);
        if (t1.equals(new IntType())) {
            if (t2.equals(new IntType())) return new IntType();
            else throw new MyException("second operand is not an integer");
        } else throw new MyException("first operand is not an integer");
    }

    private void checkTypes(Value leftTerm, Value rightTerm, Type type) throws MyException {
        if (!leftTerm.getType().equals(type) || !rightTerm.getType().equals(type)) {
            throw new ExpressionEvaluationException("Wrong types for operator " + operator);
        }
    }

    private Value evaluateArithExpr(IntegerValue leftValue, IntegerValue rightValue, String operator) throws MyException{
        switch (operator) {
            case "+":
                return new IntegerValue(leftValue.value() + rightValue.value());
            case "-":
                return new IntegerValue(leftValue.value() - rightValue.value());
            case "*":
                return new IntegerValue(leftValue.value() * rightValue.value());
            case "/":
                if (rightValue.value() == 0) {
                    throw new DivisionByZeroException("Division by zero");
                }
                return new IntegerValue(leftValue.value() / rightValue.value());
            default:
                throw new ExpressionEvaluationException("Unknown operator " + operator);
        }
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }


}