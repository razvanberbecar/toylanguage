package model.type;
import model.value.Value;

public interface Type{
    Value defaultValue();

    @Override
    String toString();

    @Override
    boolean equals(Object another);


}