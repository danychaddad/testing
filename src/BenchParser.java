import java.nio.file.*;
import java.util.*;

class BenchParser {
    Circuit parse(String fileName) throws Exception {
        Map<String, Boolean> inputs = new TreeMap<>(Comparator.comparingInt(Integer::valueOf));
        Map<String, Gate> gates = new TreeMap<>(Comparator.comparingInt(Integer::valueOf));
        List<String> outputs = new ArrayList<>();

        List<String> lines = Files.readAllLines(Paths.get(fileName));
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("#")) continue;
            if (line.startsWith("INPUT(")) {
                extractInputFromLine(line, inputs);
            } else if (line.startsWith("OUTPUT(")) {
                extractOutputFromLine(line, outputs);
            } else if (line.contains("=")) {
                extractGateFromLine(line, gates);
            }
        }
        return new Circuit(inputs, gates, outputs);
    }

    private static void extractGateFromLine(String line, Map<String, Gate> gates) {
        String[] parts = line.split("=");
        String output = parts[0].trim();
        String[] gateParts = parts[1].trim().split("\\(");
        GateType type = GateType.valueOf(gateParts[0].trim());
        List<String> gateInputs = Arrays.stream(gateParts[1]
                        .replace(")", "")
                        .split(","))
                .map(String::trim)
                .toList();
        gates.put(output, new Gate(output, type, gateInputs));
    }

    private static void extractOutputFromLine(String line, List<String> outputs) {
        String outputName = line.substring(7, line.length() - 1).trim();
        outputs.add(outputName);
    }

    private static void extractInputFromLine(String line, Map<String, Boolean> inputs) {
        String inputName = line.substring(6, line.length() - 1).trim();
        inputs.put(inputName, false);
    }
}
