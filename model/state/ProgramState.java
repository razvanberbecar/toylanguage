package model.state;

import model.statement.Statement;
import java.util.Objects;
import model.exception.MyException;

public class ProgramState {
    private static int lastId = 0;
    private static synchronized int getNewId() { return ++lastId; }

    private final int id;
    private final ExecutionStack executionStack;
    private final SymbolTable symbolTable;
    private final Out out;
    private final FileTable fileTable;
    private final Heap heap;

    public ProgramState(ExecutionStack executionStack, SymbolTable symbolTable, Out out, Statement prg, FileTable fileTable, Heap heap) {
        this.executionStack = executionStack;
        this.symbolTable = symbolTable;
        this.out = out;
        this.fileTable = fileTable;
        this.heap = heap;
        this.executionStack.push(prg);
        this.id = getNewId();
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public ExecutionStack getExecutionStack() {
        return executionStack;
    }

    public Out getListOut() {
        return out;
    }

    public FileTable getFileTable() {
        return fileTable;
    }

    public Heap getHeap() {
        return heap;
    }

    public boolean isNotCompleted(){
        return !(executionStack.isEmpty());
    }

    public int getId() { return id; }

    public ProgramState oneStep() throws MyException {
        if (executionStack.isEmpty()) {
            throw new model.exception.EmptyStackException("prgstate stack is empty");
        }
        model.statement.Statement crtStmt = executionStack.pop();
        ProgramState res = crtStmt.execute(this);
        // Only propagate a new ProgramState (e.g., from fork). For regular statements return null.
        if (res != null && res != this) return res;
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var comp = (ProgramState) obj;
        return Objects.equals(comp.getExecutionStack(), this.getExecutionStack()) &&
                Objects.equals(comp.getSymbolTable(), this.getSymbolTable()) &&
                Objects.equals(comp.getListOut(), this.getListOut()) &&
                Objects.equals(comp.getFileTable(), this.getFileTable()) &&
                Objects.equals(comp.getHeap(), this.getHeap());
    }


    @Override
    public String toString() {
        return "ProgramState id=" + id + " [" + "\n" +
                "Execution Stack =" + executionStack + "\n" +
                "Symbol Table= " + symbolTable + "\n" +
                "List Out= " + out + "\n" +
                "File Table= " + fileTable + "\n" +
                "Heap= " + heap +
                "]" + "\n";
    }
}
