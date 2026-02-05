package model.state;

import model.value.Value;
import java.util.Map;

public interface Heap {
    int allocate(Value value);
    Value get(int address);
    void update(int address, Value value);
    boolean isDefined(int address);
    void setContent(Map<Integer, Value> newContent);
    Map<Integer, Value> getContent();
}
