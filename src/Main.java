import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Main {
    public static void runAllCombinations(Simulator simulator, BenchParser parser) {
        List<String> inputLabels = parser.inputs.keySet().stream().sorted().toList();
        int numInputs = inputLabels.size();
        int numCombinations = 1 << numInputs;

        System.out.println(String.join(" | ", inputLabels) + " | " + String.join(" | ", parser.outputs));
        System.out.println("-".repeat((numInputs + parser.outputs.size()) * 4));

        for (int i = 0; i < numCombinations; i++) {
            Map<String, Boolean> testInputs = new HashMap<>();
            for (int j = 0; j < numInputs; j++) {
                boolean value = (i & (1 << j)) != 0;
                testInputs.put(inputLabels.get(j), value);
            }
            simulator.runSimulation(testInputs, parser.gates, parser.outputs);
            Map<String, Boolean> results = simulator.getValues();
            String inputValues = inputLabels.stream().map(label -> testInputs.get(label) ? "1" : "0").reduce((a, b) -> a + " | " + b).orElse("");
            String outputValues = parser.outputs.stream().map(output -> results.getOrDefault(output, false) ? "1" : "0").reduce((a, b) -> a + " | " + b).orElse("");
            System.out.println(inputValues + " | " + outputValues);
        }
    }

    public static void main(String[] args) throws Exception {
        BenchParser parser = new BenchParser();
        parser.parse("C:\\Users\\Nicolas\\Desktop\\Testing Project\\testing\\circuit.bench");
        Simulator simulator = new Simulator();
        runAllCombinations(simulator, parser);

        // Generate all possible test vectors for the circuit's inputs
        List<String> inputLabels = parser.inputs.keySet().stream().sorted().toList();
        int numInputs = inputLabels.size();
        int numCombinations = 1 << numInputs;
        List<Map<String, Integer>> testVectors = new ArrayList<>();

        for (int i = 0; i < numCombinations; i++) {
            Map<String, Integer> testVector = new HashMap<>();
            for (int j = 0; j < numInputs; j++) {
                int value = (i & (1 << j)) != 0 ? 1 : 0;
                testVector.put(inputLabels.get(j), value);
            }
            testVectors.add(testVector);
        }

        // Generate faults for outputs and fan-outs
        List<String> faults = parser.outputs.stream()
                .flatMap(output -> List.of(output + "-SA0", output + "-SA1").stream())
                .collect(Collectors.toList());

        // Identify fan-outs
        Map<String, Long> fanOuts = parser.gates.values().stream()
                .flatMap(gate -> gate.inputs.stream())
                .collect(Collectors.groupingBy(input -> input, Collectors.counting()));

        fanOuts.entrySet().stream()
                .filter(entry -> entry.getValue() > 1)
                .forEach(entry -> {
                    String fanOut = entry.getKey();
                    faults.add(fanOut + "-SA0");
                    faults.add(fanOut + "-SA1");
                });

        ParallelFaultTesting.runFaultSimulation(parser.gates, parser.inputs, parser.outputs, testVectors, faults);
    }
}