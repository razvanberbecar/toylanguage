package model.state;

import model.exception.EmptyStackException;
import model.statement.Statement;
import java.util.Stack;

public interface ExecutionStack {
    void push(Statement statement);
    boolean isEmpty();
    Statement pop() throws EmptyStackException;
}
