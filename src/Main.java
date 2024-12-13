import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        BenchParser parser = new BenchParser();
        Simulator simulator = new Simulator();

        try {
            parser.parse("circuit.bench");

            // Initialize inputs with explicit Boolean values
            Map<String, Boolean> testInputs = new HashMap<>();
            testInputs.put("1", Boolean.TRUE);
            testInputs.put("2", Boolean.TRUE);
            testInputs.put("3", Boolean.TRUE);
            testInputs.put("6", Boolean.TRUE);
            testInputs.put("7", Boolean.TRUE);
            System.out.println("Test Inputs: " + testInputs);

            simulator.runSimulation(testInputs, parser.gates, parser.outputs);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

