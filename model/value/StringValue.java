package model.value;

import model.type.StringType;
import model.type.Type;

public record StringValue(String value) implements Value{
    @Override
    public Type getType() {return new StringType();}

    @Override
    public boolean equals (Object obj){
        if (obj instanceof StringValue other){
            return this.value.equals(other.value);
        }
        return false;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }

}
