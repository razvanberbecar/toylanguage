package model.state;

import model.exception.UndefinedVariableException;
import model.exception.VariableAlreadyDefinedException;
import model.type.Type;
import model.value.Value;
import java.util.HashMap;
import java.util.Map;

public class MapSymbolTable implements SymbolTable {

    private final Map<String, Value> map = new HashMap<>();

    @Override
    public boolean isDefined(String variableName) {
        return map.containsKey(variableName);
    }

    @Override
    public Type getType(String variableName) {
        return map.get(variableName).getType();
    }

    @Override
    public void declareVariable(String variableName, Type type) throws VariableAlreadyDefinedException{
        if (isDefined(variableName)) { throw new VariableAlreadyDefinedException(variableName);} 
        map.put(variableName, type.defaultValue());
    }

    @Override
    public void update(String variableName, Value value) throws UndefinedVariableException{
        if (!isDefined(variableName)) throw new UndefinedVariableException("undefined");
        map.put(variableName, value);
    }

    @Override
    public Value getValue(String variableName) {
        return map.get(variableName);
    }

    @Override
    public Map<String, Value> getContent() {
        return map;
    }

    @Override
    public SymbolTable copy() {
        MapSymbolTable copy = new MapSymbolTable();

        for (Map.Entry<String, Value> e : map.entrySet()) {
            copy.map.put(e.getKey(), e.getValue());
        }
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Value> entry : map.entrySet()) {
            sb.append(entry.getKey())
                    .append(" -> ")
                    .append(entry.getValue()).append(", ");
        }
        return sb.toString();
    }
}
