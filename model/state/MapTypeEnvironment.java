package model.state;

import model.exception.MyException;
import model.type.Type;

import java.util.HashMap;
import java.util.Map;

public class MapTypeEnvironment implements TypeEnvironment {
    private final Map<String, Type> map = new HashMap<>();

    @Override
    public boolean isDefined(String name) {
        return map.containsKey(name);
    }

    @Override
    public Type lookup(String name) throws MyException {
        if (!isDefined(name)) {
            throw new MyException("Typecheck: variable '" + name + "' not declared");
        }
        return map.get(name);
    }

    @Override
    public void add(String name, Type type) {
        map.put(name, type);
    }

    @Override
    public TypeEnvironment cloneEnv() {
        MapTypeEnvironment copy = new MapTypeEnvironment();
        for (Map.Entry<String, Type> e : map.entrySet()) {
            copy.map.put(e.getKey(), e.getValue());
        }
        return copy;
    }
}
