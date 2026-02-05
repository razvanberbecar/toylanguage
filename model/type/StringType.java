package model.type;

import model.value.Value;
import model.value.StringValue;

public class StringType implements Type{
    @Override
    public boolean equals(Object another) {
        return another instanceof StringType;
    }

    @Override
    public String toString() {
        return "String";
    }

    @Override
    public Value defaultValue() {
        return new StringValue("");
    }
}
