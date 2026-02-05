package model.value;

import model.type.IntType;
import model.type.Type;

public record IntegerValue(int value) implements Value {

    @Override
    public Type getType() {
        return new IntType();
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }
}
