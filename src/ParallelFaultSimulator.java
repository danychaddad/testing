import java.util.*;
import java.util.stream.Collectors;

class ParallelFaultSimulator {
    private final Simulator simulator;
    private final FaultInjector faultInjector;
    private final Circuit circuit;
    private static int WORD_LENGTH = 32;
    private int faultCount = 0;
    public Set<String> detectedFaults = new HashSet<>();


    ParallelFaultSimulator(Simulator simulator, Circuit circuit) {
        this.simulator = simulator;
        this.circuit = circuit;
        this.faultInjector = new FaultInjector(circuit);
    }

    public int getFaultCount() {
        return faultCount;
    }

    void runAllFaultSimulations(int wordLength) {
        WORD_LENGTH = wordLength;
        List<String> faultNodes = generateAllFaultNodes();
        runWithFaults(faultNodes);
    }

    void runAllFaultSimulations() {
        List<String> faultNodes = generateAllFaultNodes();
        runWithFaults(faultNodes);
    }

    private List<String> generateAllFaultNodes() {
        List<String> faultNodes = new ArrayList<>();
        faultNodes.addAll(circuit.getInputs().keySet());
        faultNodes.addAll(circuit.getGates().keySet());

        for (Gate gate : circuit.getGates().values()) {
            faultNodes.addAll(gate.getInputs());
        }
        List<String> distinctFaultNodes = faultNodes.stream().distinct().collect(Collectors.toList());
        faultCount = distinctFaultNodes.size() * 2;
        return distinctFaultNodes;
    }

    void runWithFaults(List<String> faultNodes) {
        List<String> inputLabels = new ArrayList<>(circuit.getInputs().keySet());
        int numCombinations = 1 << inputLabels.size();

        for (int i = 0; i < numCombinations; i++) {
            Map<String, Boolean> inputs = getInputVector(i, inputLabels);
            simulator.runSimulation(inputs, circuit.getGates(), circuit.getOutputs());
            String correctOutputs = getOutputs(simulator, circuit);

            String inputValues = inputLabels.stream()
                    .map(label -> inputs.get(label) ? "1" : "0")
                    .collect(Collectors.joining(" | "));

            System.out.printf("\nTest Vector: %s\n", inputValues);
            System.out.printf("%-5s | %-20s | %-20s | %-20s%n", "Pass", "Inputs", "Fault", "Outputs");
            System.out.println("-".repeat(75));

            int faultCount = 1; // Include fault-free simulation as the first count
            int currentPass = 1; // Start with Pass = 1

            // Print fault-free outputs
            System.out.printf("%-5s | %-20s | %-20s | %-20s%n", currentPass, inputValues, "fault free outputs", correctOutputs);

            for (String node : faultNodes) {
                for (boolean faultValue : List.of(true, false)) {
                    if (faultCount >= WORD_LENGTH) {
                        faultCount = 0; // Reset the fault counter
                        currentPass++;  // Increment the pass number
                    }

                    Map<String, Boolean> faultyInputs = faultInjector.injectStuckAtFault(inputs, node, faultValue);

                    simulator.runSimulation(faultyInputs, circuit.getGates(), circuit.getOutputs());
                    String faultyOutputs = getOutputs(simulator, circuit);
                    String baseString = String.format("\nNode: %s Stuck-At: %d\n", node, faultValue ? 1 : 0);
                    if (!Objects.equals(correctOutputs, faultyOutputs)) {
                        detectedFaults.add(baseString);
                    }
                    String faultDescription = String.format("stuck-at-%d on %s", faultValue ? 1 : 0, node);
                    System.out.printf("%-5s | %-20s | %-20s | %-20s%n", currentPass, inputValues, faultDescription, faultyOutputs);

                    faultCount++; // Increment the fault count
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
