package model.expression;

import model.state.Heap;
import model.state.SymbolTable;
import model.state.TypeEnvironment;
import model.type.Type;
import model.value.Value;

public record ConstantExpression (Value value) implements Expression{

    @Override
    public Value evaluate(SymbolTable symTable, Heap hp) {
        return value;
    }

    @Override
    public Type typecheck(TypeEnvironment typeEnv){
        return value.getType();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
