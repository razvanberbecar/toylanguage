package model.expression;

import model.exception.MyException;
import model.exception.UndefinedVariableException;
import model.state.Heap;
import model.state.SymbolTable;
import model.state.TypeEnvironment;
import model.type.Type;
import model.value.Value;

public record VariableExpression (String variableName)implements Expression{

    @Override
    public Value evaluate(SymbolTable symTable, Heap hp) throws UndefinedVariableException{
        if(!symTable.isDefined(variableName)){
            throw new UndefinedVariableException("Variable not defined");
        }
        return symTable.getValue(variableName);
    }

    @Override
    public Type typecheck(TypeEnvironment typeEnv) throws MyException {
        return typeEnv.lookup(variableName);
    }

    @Override
    public String toString() {
        return variableName;
    }
}
