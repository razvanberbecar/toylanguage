package model.state;

import model.exception.UndefinedVariableException;
import model.exception.VariableAlreadyDefinedException;
import model.type.Type;
import model.value.Value;

import java.util.Map;

public interface SymbolTable {
    boolean isDefined(String variableName);

    Type getType(String variableName);

    void declareVariable(String variableName, Type type) throws VariableAlreadyDefinedException;

    void update(String variableName, Value value) throws UndefinedVariableException;

    Value getValue(String variableName);

    Map<String, Value> getContent();

    SymbolTable copy();
}
