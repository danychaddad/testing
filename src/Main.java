import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        BenchParser parser = new BenchParser();
        Circuit circuit = parser.parse("circuit.bench");
        Simulator simulator = new Simulator();
        SerialFaultSimulator serialFaultSimulator = new SerialFaultSimulator(simulator, circuit);

        Scanner scanner = new Scanner(System.in);
        List<String> inputLabels = new ArrayList<>(circuit.getInputs().keySet());

        String testVector;
        while (true) {
            System.out.printf("The circuit expects %d inputs.%n", inputLabels.size());
            System.out.println("Enter a test vector (e.g., 1010):");
            testVector = scanner.next().trim();
            if (validateTestVector(testVector, inputLabels.size())) {
                break;
            }
            System.out.println("Invalid test vector. Please enter exactly " + inputLabels.size() + " binary digits.");
        }

        System.out.println("Running True-Value Simulation...");
        runTrueValueSimulation(simulator, circuit, testVector);

        System.out.println("Enter fault nodes separated by spaces (e.g., 1 3 5):");
        scanner.nextLine();
        String faultNodesInput = scanner.nextLine().trim();
        List<String> faultNodes = Arrays.asList(faultNodesInput.split(" "));

        if (!validateFaultNodes(faultNodes, circuit)) {
            System.out.println("Invalid nodes entered. Please enter valid node names from the circuit.");
            return;
        }

        System.out.println("Running Fault Simulation with Selected Nodes...");
        serialFaultSimulator.runWithFaults(faultNodes);
    }

    private static void runTrueValueSimulation(Simulator simulator, Circuit circuit, String testVector) {
        List<String> inputLabels = new ArrayList<>(circuit.getInputs().keySet());
        Map<String, Boolean> inputs = parseTestVector(testVector, inputLabels);

        simulator.runSimulation(inputs, circuit.getGates(), circuit.getOutputs());
        String outputValues = circuit.getOutputs().stream()
                .map(out -> simulator.getValues().getOrDefault(out, false) ? "1" : "0")
                .collect(Collectors.joining(" | "));

        System.out.println("Correct Inputs  | Outputs");
        System.out.println("-".repeat(40));
        String inputValues = inputLabels.stream()
                .map(label -> inputs.get(label) ? "1" : "0")
                .collect(Collectors.joining(" | "));
        System.out.printf("%-15s | %-15s%n", inputValues, outputValues);
    }

    private static Map<String, Boolean> parseTestVector(String testVector, List<String> inputLabels) {
        Map<String, Boolean> testInputs = new HashMap<>();
        for (int i = 0; i < inputLabels.size(); i++) {
            boolean value = testVector.charAt(i) == '1';
            testInputs.put(inputLabels.get(i), value);
        }
        return testInputs;
    }

    private static boolean validateTestVector(String testVector, int numInputs) {
        return testVector.length() == numInputs && testVector.matches("[01]+");
    }

    private static boolean validateFaultNodes(List<String> faultNodes, Circuit circuit) {
        return faultNodes.stream().allMatch(node -> circuit.getInputs().containsKey(node) || circuit.getGates().containsKey(node));
    }
}
