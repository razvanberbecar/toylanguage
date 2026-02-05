package model.statement;

import model.exception.MyException;
import model.exception.TypeMismatchException;
import model.exception.UndefinedVariableException;
import model.expression.Expression;
import model.state.ProgramState;
import model.state.SymbolTable;
import model.state.TypeEnvironment;
import model.type.Type;
import model.value.Value;

public record AssignmentStatement(Expression expression, String variableName)
        implements Statement {

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        SymbolTable symbolTable = state.getSymbolTable();
        if (!symbolTable.isDefined(variableName)) {
            throw new UndefinedVariableException("Variable not defined");
        }
        Value value = expression.evaluate(symbolTable, state.getHeap());
        if (!value.getType().equals(symbolTable.getType(variableName))) {
            throw new TypeMismatchException("Type mismatch");
        }
        symbolTable.update(variableName, value);
        return state;
    }

    @Override
    public TypeEnvironment typecheck(TypeEnvironment typeEnv) throws MyException {
        Type varType = typeEnv.lookup(variableName);
        Type expType = expression.typecheck(typeEnv);
        if (!varType.equals(expType))
            throw new MyException("Assignment: right hand side and left hand side have different types");
        return typeEnv;
    }

    @Override
    public String toString() {
        return variableName + " = " + expression;
    }
}
