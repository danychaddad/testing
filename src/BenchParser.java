import java.nio.file.*;
import java.util.*;

class BenchParser {
    Map<String, Boolean> inputs = new TreeMap<>(Comparator.comparingInt(Integer::valueOf));
    Map<String, Gate> gates = new TreeMap<>(Comparator.comparingInt(Integer::valueOf));
    List<String> outputs = new ArrayList<>();

    void parse(String fileName) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(fileName));
        for (String line : lines) {
            line = line.trim();  // Trim whitespace around the line
            if (line.startsWith("#"))
                continue;
            if (line.startsWith("INPUT(")) {
                String inputName = line.substring(6, line.length() - 1).trim();
                inputs.put(inputName, false);
            } else if (line.startsWith("OUTPUT(")) {
                String outputName = line.substring(7, line.length() - 1).trim();
                outputs.add(outputName);
            } else if (line.contains("=")) {
                System.out.println(line);
                String[] parts = line.split("=");
                String output = parts[0].trim();
                String[] gateParts = parts[1].trim().split("\\(");
                GateType type = GateType.valueOf(gateParts[0].trim());
                List<String> gateInputs = Arrays.stream(gateParts[1]
                                .replace(")", "")
                                .split(","))
                        .map(String::trim)  // Trim each input
                        .toList();
                gates.put(output, new Gate(output, type, gateInputs));
            }
        }
    }
}
