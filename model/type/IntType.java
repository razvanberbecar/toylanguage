package model.type;

import model.value.Value;
import model.value.IntegerValue;

public class IntType implements Type{

    @Override
    public boolean equals(Object another) {
        return another instanceof IntType;
    }

    @Override
    public String toString() {
        return "int";
    }

    @Override
    public Value defaultValue() {
        return new IntegerValue(0);
    }
}
