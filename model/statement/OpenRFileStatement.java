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
import java.io.FileReader;
import java.io.IOException;

public record OpenRFileStatement(Expression expression) implements Statement {

    @Override
    public ProgramState execute (ProgramState state) throws MyException {
        Value value = expression.evaluate(state.getSymbolTable(), state.getHeap());
        if (!(value.getType() instanceof StringType)){
            throw new MyException("OpenRFile: expression must be a string");
        }
        StringValue filename=(StringValue) value;

        if (state.getFileTable().isDefined(filename)){
            throw new MyException("OpenRFile: file already opened");
        }

        try {
            BufferedReader fileDescriptor = new BufferedReader(new FileReader(filename.value()));
            state.getFileTable().add(filename, fileDescriptor);
        } catch (IOException e) {
            throw new MyException("OpenRFile: error opening file");
        }
        return state;
    }

    @Override
    public TypeEnvironment typecheck(TypeEnvironment typeEnv) throws MyException {
        Type t = expression.typecheck(typeEnv);
        if (!t.equals(new StringType()))
            throw new MyException("OpenRFile: expression is not a string");
        return typeEnv;
    }

    @Override
    public String toString() {
        return "openRFile(" + expression + ")";
    }


}
