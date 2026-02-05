package model.statement;

import model.exception.MyException;
import model.exception.UndefinedVariableException;
import model.exception.VariableAlreadyDefinedException;
import model.expression.Expression;
import model.state.ProgramState;
import model.state.SymbolTable;
import model.state.TypeEnvironment;
import model.type.IntType;
import model.type.StringType;
import model.type.Type;
import model.value.IntegerValue;
import model.value.StringValue;
import model.value.Value;

import java.io.BufferedReader;
import java.io.IOException;

public record ReadFileStatement(Expression expression, String variableName) implements Statement {

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        SymbolTable symbolTable = state.getSymbolTable();

        if (!symbolTable.isDefined(variableName)) {
            throw new UndefinedVariableException("ReadFile: variable not defined: " + variableName);
        }

        if (!((Type) symbolTable.getType(variableName)).equals(new IntType())) {
            throw new MyException("ReadFile: variable must be of type int: " + variableName);
        }

        Value value = expression.evaluate(symbolTable, state.getHeap());

        if (!(value.getType() instanceof StringType)) {
            throw new MyException("ReadFile: expression must evaluate to a string");
        }

        StringValue fileName = (StringValue) value;

        if (!state.getFileTable().isDefined(fileName)) {
            throw new MyException("ReadFile: file not opened: " + fileName.value());
        }

        BufferedReader fileDescriptor = state.getFileTable().get(fileName);

        try {
            String line = fileDescriptor.readLine();
            IntegerValue intValue;

            if (line == null) {
                intValue = new IntegerValue(0);
            } else {
                try {
                    intValue = new IntegerValue(Integer.parseInt(line.trim()));
                } catch (NumberFormatException e) {
                    throw new MyException("ReadFile: cannot parse line as integer: " + line);
                }
            }

            symbolTable.update(variableName, intValue);

        } catch (IOException e) {
            throw new MyException("ReadFile: error reading from file: " + fileName.value() + " - " + e.getMessage());
        }

        return state;
    }

    @Override
    public TypeEnvironment typecheck(TypeEnvironment typeEnv) throws MyException {
        Type exprType = expression.typecheck(typeEnv);
        if (!exprType.equals(new StringType()))
            throw new MyException("ReadFile: expression is not a string");
        Type varType = typeEnv.lookup(variableName);
        if (!varType.equals(new IntType()))
            throw new MyException("ReadFile: variable is not of type int");
        return typeEnv;
    }

    @Override
    public String toString() {
        return "readFile(" + expression + ", " + variableName + ")";
    }
}