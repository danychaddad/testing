import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        BenchParser parser = new BenchParser();
        Circuit circuit = parser.parse("circuit.bench");
        Simulator simulator = new Simulator();
        SerialFaultSimulator serialFaultSimulator = new SerialFaultSimulator(simulator, circuit);
        ParallelFaultSimulator parallelFaultSimulator = new ParallelFaultSimulator(simulator, circuit);

        Scanner scanner = new Scanner(System.in);
        List<String> inputLabels = new ArrayList<>(circuit.getInputs().keySet());

//        String testVector;
//        while (true) {
//            System.out.printf("The circuit expects %d inputs.%n", inputLabels.size());
//            System.out.println("Enter a test vector (e.g., 1010):");
//            testVector = scanner.next().trim();
//            if (validateTestVector(testVector, inputLabels.size())) {
//                break;
//            }
//            System.out.println("Invalid test vector. Please enter exactly " + inputLabels.size() + " binary digits.");
//        }
//
//        System.out.println("Running True-Value Simulation...");
//        runTrueValueSimulation(simulator, circuit, testVector);

        runAllCombinations(simulator, circuit);
        long serialStartTime = System.currentTimeMillis();
        System.out.println("Running Serial Fault Simulation for All Possible Faults...");
        serialFaultSimulator.runAllFaultSimulations();
        long serialEndTime = System.currentTimeMillis();
        long serialExecutionTime = serialEndTime - serialStartTime;

        long parallelstartTime = System.currentTimeMillis();
        System.out.println("Running Parallel Fault Simulation for All Possible Faults...");
        parallelFaultSimulator.runAllFaultSimulations(4);
        long parallelEndTime = System.currentTimeMillis();
        long parallelExecutionTime = parallelEndTime - parallelstartTime;

        int numberOfFaults = circuit.getFaultSites() * 2;
        System.out.println("Total number of faults: " + numberOfFaults);
        System.out.println("Serial fault simulation execution time: " + serialExecutionTime + "ms");
        int serialFaults = serialFaultSimulator.detectedFaults.size();
        System.out.println("Serial detected fault count: " + serialFaults);
        System.out.println("Serial fault coverage: " + (double) serialFaults * 100 / numberOfFaults + "%");
        System.out.println("Parallel fault simulation execution time: " + parallelExecutionTime + "ms");
        int parallelFaults = parallelFaultSimulator.detectedFaults.size();
        System.out.println("Parallel detected fault count: " + parallelFaults);
        System.out.println("Parallel fault coverage: " + (double) parallelFaults * 100 / numberOfFaults + "%");
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
        System.out.printf("%-20s | %-20s%n", inputValues, outputValues);
    }

    private static Map<String, Boolean> parseTestVector(String testVector, List<String> inputLabels) {
        Map<String, Boolean> testInputs = new HashMap<>();
        for (int i = 0; i < inputLabels.size(); i++) {
            boolean value = testVector.charAt(i) == '1';
            testInputs.put(inputLabels.get(i), value);
        }
        return testInputs;
    }

    public static void runAllCombinations(Simulator simulator, Circuit circuit) {
        List<String> inputLabels = circuit.getInputs().keySet().stream().sorted().toList();
        int numInputs = inputLabels.size();
        int numCombinations = 1 << numInputs;

        System.out.println(String.join(" | ", inputLabels) + " | " + String.join(" | ", circuit.getOutputs()));
        System.out.println("-".repeat((numInputs + circuit.getOutputs().size()) * 4));

        for (int i = 0; i < numCombinations; i++) {
            Map<String, Boolean> testInputs = new HashMap<>();
            for (int j = 0; j < numInputs; j++) {
                boolean value = (i & (1 << j)) != 0;
                testInputs.put(inputLabels.get(j), value);
            }
            simulator.runSimulation(testInputs, circuit.getGates(), circuit.getOutputs());
            Map<String, Boolean> results = simulator.getValues();
            String inputValues = inputLabels.stream().map(label -> testInputs.get(label) ? "1" : "0").reduce((a, b) -> a + " | " + b).orElse("");
            String outputValues = circuit.getOutputs().stream().map(output -> results.getOrDefault(output, false) ? "1" : "0").reduce((a, b) -> a + " | " + b).orElse("");
            System.out.println(inputValues + " | " + outputValues);
        }
    }

    private static boolean validateTestVector(String testVector, int numInputs) {
        return testVector.length() == numInputs && testVector.matches("[01]+");
    }
}