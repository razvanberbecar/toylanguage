package model.statement;

import model.exception.MyException;
import model.expression.Expression;
import model.state.ProgramState;
import model.state.TypeEnvironment;
import model.type.StringType;
import model.type.Type;
import model.value.StringValue;
import model.value.Value;

import java.io.BufferedReader;
import java.io.IOException;

public record CloseRFileStatement(Expression expression) implements Statement {

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        Value value = expression.evaluate(state.getSymbolTable(), state.getHeap());

        if (!(value.getType() instanceof StringType)) {
            throw new MyException("CloseRFile: expression must evaluate to a string");
        }

        StringValue fileName = (StringValue) value;

        if (!state.getFileTable().isDefined(fileName)) {
            throw new MyException("CloseRFile: file not opened: " + fileName.value());
        }

        BufferedReader fileDescriptor = state.getFileTable().get(fileName);

        try {
            fileDescriptor.close();
        } catch (IOException e) {
            throw new MyException("CloseRFile: error closing file: " + fileName.value() + " - " + e.getMessage());
        }

        state.getFileTable().remove(fileName);

        return state;
    }

    @Override
    public TypeEnvironment typecheck(TypeEnvironment typeEnv) throws MyException {
        Type t = expression.typecheck(typeEnv);
        if (!t.equals(new StringType()))
            throw new MyException("CloseRFile: expression is not a string");
        return typeEnv;
    }

    @Override
    public String toString() {
        return "closeRFile(" + expression + ")";
    }
}