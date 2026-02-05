package model.statement;

import model.exception.MyException;
import model.state.*;

public record ForkStatement(Statement statement) implements Statement {
    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        ExecutionStack childStack = new StackExecutionStack();

        SymbolTable childSymTable = state.getSymbolTable().copy();
        Out out = state.getListOut();
        FileTable fileTable = state.getFileTable();
        Heap heap = state.getHeap();

        return new ProgramState(childStack, childSymTable, out, statement, fileTable, heap);
    }

    @Override
    public model.state.TypeEnvironment typecheck(model.state.TypeEnvironment typeEnv) throws MyException {
        statement.typecheck(typeEnv.cloneEnv());
        return typeEnv;
    }

    @Override
    public String toString() {
        return "fork(" + statement + ")";
    }
}
