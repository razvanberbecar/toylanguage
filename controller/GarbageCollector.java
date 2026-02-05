package controller;

import model.value.RefValue;
import model.value.Value;

import java.util.*;
import java.util.stream.Collectors;

public class GarbageCollector {

    public static Map<Integer, Value> safeGarbageCollector(List<Integer> referencedAddresses, Map<Integer, Value> heap) {
        return heap.entrySet().stream()
                .filter(e -> referencedAddresses.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static List<Integer> getAddrFromSymTable(Collection<Value> symTableValues, Map<Integer, Value> heap) {
        // Use a Set to avoid duplicates and improve performance on contains checks
        Set<Integer> reachable = symTableValues.stream()
                .filter(v -> v instanceof RefValue)
                .map(v -> ((RefValue) v).getAddress())
                .collect(Collectors.toCollection(LinkedHashSet::new));

        boolean changes = true;
        while (changes) {
            changes = false;
            // For every address currently reachable, if the heap stores a RefValue at that address,
            // add its inner address; repeat until a fixed point
            List<Integer> newAddresses = heap.entrySet().stream()
                    .filter(e -> reachable.contains(e.getKey()))
                    .filter(e -> e.getValue() instanceof RefValue)
                    .map(e -> ((RefValue) e.getValue()).getAddress())
                    .filter(addr -> !reachable.contains(addr))
                    .collect(Collectors.toList());

            if (!newAddresses.isEmpty()) {
                reachable.addAll(newAddresses);
                changes = true;
            }
        }
        // Preserve previous return type/API
        return new ArrayList<>(reachable);
    }

}
