package model.statement;

import model.state.ProgramState;
import model.state.TypeEnvironment;

public class NoOperationStatement implements Statement {
    @Override
    public ProgramState execute(ProgramState state) {
        return state;
    }

    @Override
    public TypeEnvironment typecheck(TypeEnvironment typeEnv) {
        return typeEnv;
    }
}
