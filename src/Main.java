import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void runAllCombinations(Simulator simulator, BenchParser parser) {
        List<String> inputLabels = parser.inputs.keySet().stream().sorted().toList();
        int numInputs = inputLabels.size();
        int numCombinations = 1 << numInputs;

        System.out.println(String.join(" | ", inputLabels) + " | " + String.join(" | ", parser.outputs));
        System.out.println("-".repeat((numInputs + parser.outputs.size()) * 4));

        for (int i = 0; i < numCombinations; i++) {
            Map<String, Boolean> testInputs = new TreeMap<>();
            for (int j = 0; j < numInputs; j++) {
                // Reverse the bit order
                boolean value = (i & (1 << (numInputs - j - 1))) != 0;
                testInputs.put(inputLabels.get(j), value);
            }
            simulator.runSimulation(testInputs, parser.gates, parser.outputs);
            Map<String, Boolean> results = simulator.values;

            String inputValues = inputLabels.stream()
                    .map(label -> testInputs.get(label) ? "1" : "0")
                    .collect(Collectors.joining(" | "));

            String outputValues = parser.outputs.stream()
                    .map(output -> results.getOrDefault(output, false) ? "1" : "0")
                    .collect(Collectors.joining(" | "));
            System.out.println(inputValues + " | " + outputValues);
        }

    }

    public static void main(String[] args) throws Exception {
        BenchParser parser = new BenchParser();
        parser.parse("circuit.bench");
        Simulator simulator = new Simulator();
        runAllCombinations(simulator, parser);
    }
}

