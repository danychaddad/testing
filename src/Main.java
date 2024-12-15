import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void runAllCombinations(Simulator simulator, Circuit circuit) {
        List<String> inputLabels = circuit.getInputs().keySet().stream().sorted().toList();
        int numInputs = inputLabels.size();
        int numCombinations = getNumberOfCombinations(numInputs);
        displayTruthTableHeader(circuit, inputLabels, numInputs);

        for (int i = 0; i < numCombinations; i++) {
            Map<String, Boolean> testInputs = getInputVector(numInputs, i, inputLabels);
            simulator.runSimulation(testInputs, circuit.getGates(), circuit.getOutputs());

            Map<String, Boolean> results = simulator.getValues();
            displayTruthTable(circuit, inputLabels, testInputs, results);
        }
    }

    private static void displayTruthTable(Circuit circuit, List<String> inputLabels, Map<String, Boolean> testInputs, Map<String, Boolean> results) {
        String inputValues = inputLabels.stream()
                .map(label -> testInputs.get(label) ? "1" : "0")
                .collect(Collectors.joining(" | "));

        String outputValues = circuit.getOutputs().stream()
                .map(output -> results.getOrDefault(output, false) ? "1" : "0")
                .collect(Collectors.joining(" | "));
        System.out.println(inputValues + " | " + outputValues);
    }

    private static Map<String, Boolean> getInputVector(int numInputs, int i, List<String> inputLabels) {
        Map<String, Boolean> testInputs = new TreeMap<>();
        for (int j = 0; j < numInputs; j++) {
            boolean value = (i & (1 << (numInputs - j - 1))) != 0;
            testInputs.put(inputLabels.get(j), value);
        }
        return testInputs;
    }

    private static void displayTruthTableHeader(Circuit circuit, List<String> inputLabels, int numInputs) {
        System.out.println(String.join(" | ", inputLabels) + " | " + String.join(" | ", circuit.getOutputs()));
        System.out.println("-".repeat((numInputs + circuit.getOutputs().size()) * 4));
    }

    private static int getNumberOfCombinations(int numInputs) {
        return 1 << numInputs;
    }

    public static void main(String[] args) throws Exception {
        BenchParser parser = new BenchParser();
        Circuit circuit = parser.parse("circuit.bench");
        Simulator simulator = new Simulator();
        runAllCombinations(simulator, circuit);
    }
}
