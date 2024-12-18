import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Simulator {
    Map<String, Boolean> values = new HashMap<>();

    boolean evaluateGate(Gate gate) {
        // Check for null inputs
        if (gate.inputs.stream().anyMatch(input -> !values.containsKey(input) || values.get(input) == null)) {
            throw new IllegalArgumentException("Undefined or null input detected: " + gate.inputs);
        }

        // Fetch input values
        List<Boolean> inputValues = gate.inputs.stream()
                .map(input -> values.getOrDefault(input, Boolean.FALSE))
                .toList();

        // Evaluate based on gate type
        return switch (gate.type) {
            case AND -> inputValues.stream().allMatch(Boolean::booleanValue);
            case OR -> inputValues.stream().anyMatch(Boolean::booleanValue);
            case NOT -> !inputValues.getFirst();
            case NAND -> !inputValues.stream().allMatch(Boolean::booleanValue);
            case NOR -> inputValues.stream().noneMatch(Boolean::booleanValue);
            case XOR -> inputValues.stream().reduce(false, (a, b) -> a ^ b);
            case BUFF -> inputValues.getFirst();
        };
    }

    void runSimulation(Map<String, Boolean> inputs, Map<String, Gate> gates, List<String> outputs) {
        if (inputs == null || gates == null || outputs == null) {
            throw new IllegalArgumentException("Inputs, gates, and outputs cannot be null.");
        }
        values.clear();
        values.putAll(inputs);
        for (Gate gate : gates.values()) {
            values.put(gate.output, evaluateGate(gate));
        }
    }
}


