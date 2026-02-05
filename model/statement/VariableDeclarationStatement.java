package model.statement;

import model.exception.MyException;
import model.exception.VariableAlreadyDefinedException;
import model.state.ProgramState;
import model.state.TypeEnvironment;
import model.type.Type;

public record VariableDeclarationStatement(Type type, String variableName) implements Statement {

    @Override
    public ProgramState execute(ProgramState state) throws VariableAlreadyDefinedException {
        var symbolTable = state.getSymbolTable();
        if (symbolTable.isDefined(variableName)) {
            throw new VariableAlreadyDefinedException("Variable already defined");
        }
        symbolTable.declareVariable(variableName, type);
        return state;
    }

    @Override
    public TypeEnvironment typecheck(TypeEnvironment typeEnv) throws MyException {
        typeEnv.add(variableName, type);
        return typeEnv;
    }

    @Override
    public String toString() {
        return "declare " + variableName + " as " + type;
    }
}
