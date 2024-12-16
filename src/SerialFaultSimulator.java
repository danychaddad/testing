import java.util.*;
import java.util.stream.Collectors;

class SerialFaultSimulator {
    private final Simulator simulator;
    private final FaultInjector faultInjector;
    private final Circuit circuit;

    SerialFaultSimulator(Simulator simulator, Circuit circuit) {
        this.simulator = simulator;
        this.circuit = circuit;
        this.faultInjector = new FaultInjector(circuit);
    }

    void runWithFaults(List<String> faultNodes) {
        List<String> inputLabels = new ArrayList<>(circuit.getInputs().keySet());
        int numCombinations = 1 << inputLabels.size();

        for (String node : faultNodes) {
            for (boolean faultValue : List.of(true, false)) {
                System.out.printf("\nNode: %s Stuck-At: %d\n", node, faultValue ? 1 : 0);
                System.out.printf("%-20s | %-20s | %-15s%n", "Inputs", "Faulty Outputs", "Correct Outputs");
                System.out.println("-".repeat(60));

                for (int i = 0; i < numCombinations; i++) {
                    Map<String, Boolean> inputs = getInputVector(i, inputLabels);
                    simulator.runSimulation(inputs, circuit.getGates(), circuit.getOutputs());
                    String correctOutputs = getOutputs(simulator, circuit);

                    Map<String, Boolean> faultyInputs = faultInjector.injectStuckAtFault(inputs, node, faultValue);
                    simulator.runSimulation(faultyInputs, circuit.getGates(), circuit.getOutputs());
                    String faultyOutputs = getOutputs(simulator, circuit);

                    String inputValues = inputLabels.stream()
                            .map(label -> inputs.get(label) ? "1" : "0")
                            .collect(Collectors.joining(" | "));

                    System.out.printf("%-20s | %-20s | %-15s%n", inputValues, faultyOutputs, correctOutputs);
                }
            }
        }
    }

    private Map<String, Boolean> getInputVector(int combination, List<String> inputLabels) {
        Map<String, Boolean> inputs = new HashMap<>();
        for (int i = 0; i < inputLabels.size(); i++) {
            boolean value = (combination & (1 << (inputLabels.size() - i - 1))) != 0;
            inputs.put(inputLabels.get(i), value);
        }
        return inputs;
    }

    private String getOutputs(Simulator simulator, Circuit circuit) {
        return circuit.getOutputs().stream()
                .map(out -> simulator.getValues().getOrDefault(out, false) ? "1" : "0")
                .collect(Collectors.joining(" | "));
    }
}