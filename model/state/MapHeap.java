package model.state;

import model.value.Value;
import java.util.HashMap;
import java.util.Map;

public class MapHeap implements Heap{
    private Map<Integer, Value> heap;
    private int nextFreeLocation;

    public MapHeap(){
        heap =new HashMap<>();
        nextFreeLocation = 1;
    }

    @Override
    public int allocate(Value value) {
        int address=nextFreeLocation;
        heap.put(address, value);
        nextFreeLocation++;
        return address;
    }

    @Override
    public Value get(int address) {
        return heap.get(address);
    }

    @Override
    public boolean isDefined(int adress){
        return heap.containsKey(adress);
    }

    @Override
    public void setContent(Map<Integer, Value> newContent){
        heap = newContent;
    }

    @Override
    public Map<Integer, Value> getContent(){
        return heap;
    }

    @Override
    public void update(int address, Value value){
        heap.put(address, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, Value> entry : heap.entrySet()) {
            sb.append(entry.getKey())
                    .append(" -> ")
                    .append(entry.getValue())
                    .append(", ");
        }
        return sb.toString();
    }
}
