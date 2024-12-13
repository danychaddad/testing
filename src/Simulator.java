import java.util.*;

public class Simulator {
    private final Map<String, Boolean> values = new HashMap<>();

    public boolean evaluateGate(Gate gate) {
        if (gate.inputs.stream().anyMatch(input -> !values.containsKey(input) || values.get(input) == null)) {
            throw new IllegalArgumentException("Undefined or null input detected: " + gate.inputs);
        }

        List<Boolean> inputValues = gate.inputs.stream()
                .map(input -> values.getOrDefault(input, Boolean.FALSE))
                .toList();

        return switch (gate.type) {
            case AND -> inputValues.stream().allMatch(Boolean::booleanValue);
            case OR -> inputValues.stream().anyMatch(Boolean::booleanValue);
            case NOT -> !inputValues.get(0);
            case NAND -> !inputValues.stream().allMatch(Boolean::booleanValue);
            case NOR -> inputValues.stream().noneMatch(Boolean::booleanValue);
            case XOR -> inputValues.stream().reduce(false, (a, b) -> a ^ b);
            case BUFF -> inputValues.get(0);
        };
    }

    public void runSimulation(Map<String, Boolean> inputs, Map<String, Gate> gates, List<String> outputs) {
        values.clear();
        values.putAll(inputs);
        for (Gate gate : gates.values()) {
            values.put(gate.output, evaluateGate(gate));
        }
    }

    public Map<String, Boolean> getValues() {
        return new HashMap<>(values);
    }
}