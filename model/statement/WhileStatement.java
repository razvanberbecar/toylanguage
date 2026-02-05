package model.statement;

import model.exception.MyException;
import model.expression.Expression;
import model.state.ProgramState;
import model.state.TypeEnvironment;
import model.type.BoolType;
import model.type.Type;

public record WhileStatement(Expression condition, Statement body) implements Statement {
    @Override
    public ProgramState execute(ProgramState state) throws MyException {
        state.getExecutionStack().push(
                new IfStatement(
                        condition,
                        new CompoundStatement(body, this),
                        new NoOperationStatement()
                )
        );
        return state;
    }

    @Override
    public TypeEnvironment typecheck(TypeEnvironment typeEnv) throws MyException {
        Type condType = condition.typecheck(typeEnv);
        if (!condType.equals(new BoolType()))
            throw new MyException("The WHILE condition is not a boolean");
        body.typecheck(typeEnv.cloneEnv());
        return typeEnv;
    }

    @Override
    public String toString() {
        return "while(" + condition + ") " + body;
    }
}
