package model.statement;

import model.exception.MyException;
import model.expression.Expression;
import model.state.ProgramState;
import model.state.TypeEnvironment;
import model.type.BoolType;
import model.type.Type;
import model.value.BooleanValue;
import model.value.Value;

public record IfStatement(Expression condition, Statement thenBranch, Statement elseBranch) implements Statement {

    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        Value result = condition.evaluate(state.getSymbolTable(), state.getHeap());
        if (result instanceof BooleanValue(boolean value)) {
            if (value) {
                state.getExecutionStack().push(thenBranch);
            } else {
                state.getExecutionStack().push(elseBranch);
            }
        } else {
            throw new RuntimeException("Condition expression does not evaluate to a boolean.");
        }
        return state;
    }

    @Override
    public TypeEnvironment typecheck(TypeEnvironment typeEnv) throws MyException {
        Type condType = condition.typecheck(typeEnv);
        if (!condType.equals(new BoolType()))
            throw new MyException("The IF condition is not a boolean");
        thenBranch.typecheck(typeEnv.cloneEnv());
        elseBranch.typecheck(typeEnv.cloneEnv());
        return typeEnv;
    }

    @Override
    public String toString() {
        return "if " + condition + " then " + thenBranch + " else " + elseBranch;
    }
}
