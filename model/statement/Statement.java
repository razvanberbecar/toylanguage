package model.statement;

import model.exception.MyException;
import model.state.ProgramState;
import model.state.TypeEnvironment;

public interface Statement {
    ProgramState execute(ProgramState state) throws MyException;
    TypeEnvironment typecheck(TypeEnvironment typeEnv) throws MyException;
}
