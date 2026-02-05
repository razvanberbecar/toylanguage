package model.state;


import model.exception.EmptyStackException;
import model.statement.Statement;
import java.util.LinkedList;
import java.util.Deque;

public class StackExecutionStack implements ExecutionStack {
    private final Deque<Statement> stack = new LinkedList<>();

    @Override
    public void push(Statement statement) {
        stack.push(statement); // LIFO: add to front
    }

    @Override
    public Statement pop() throws EmptyStackException {
        if (stack.isEmpty()) throw new EmptyStackException("empty");
        return stack.pop();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (!stack.isEmpty()) {
            boolean first = true;
            for (Statement s : stack) {
                if (!first) sb.append(" | ");
                sb.append(s == null ? "null" : s.toString());
                first = false;
            }
        }

        sb.append("}");
        return sb.toString();
    }

}
