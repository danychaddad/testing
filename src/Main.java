import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            Map<String, Boolean> results = simulator.values;
            String inputValues = inputLabels.stream().map(label -> testInputs.get(label) ? "1" : "0").reduce((a, b) -> a + " | " + b).orElse("");
            String outputValues = parser.outputs.stream().map(output -> results.getOrDefault(output, false) ? "1" : "0").reduce((a, b) -> a + " | " + b).orElse("");
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

