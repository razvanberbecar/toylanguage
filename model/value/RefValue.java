package model.value;

import model.type.Type;
import model.type.RefType;

public record RefValue(int address, Type locationType) implements Value {
    @Override
    public Type getType() {
        return new RefType(locationType);
    }

    public int getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "(" + address + ", " + locationType + ")";
    }
}