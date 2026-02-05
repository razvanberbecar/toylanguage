package model.state;

import model.exception.MyException;
import model.type.Type;

public interface TypeEnvironment {
    boolean isDefined(String name);
    Type lookup(String name) throws MyException;
    void add(String name, Type type);
    TypeEnvironment cloneEnv();
}
