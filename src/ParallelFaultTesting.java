import java.util.*;

public class ParallelFaultTesting {
    public static void runFaultSimulation(
            Map<String, Gate> gates,
            Map<String, Boolean> inputs,
            List<String> outputs,
            List<Map<String, Integer>> testVectors,
            List<String> faults
    ) {
        Simulator simulator = new Simulator();
        int wordLength = 32; // Word length
        int faultsPerPass = wordLength - 1; // 31 faults per pass

        for (Map<String, Integer> testVector : testVectors) {
            // Convert test vector inputs to boolean
            Map<String, Boolean> booleanInputs = new HashMap<>();
            for (Map.Entry<String, Integer> entry : testVector.entrySet()) {
                booleanInputs.put(entry.getKey(), entry.getValue() != 0);
            }

            // Run fault-free simulation
            simulator.runSimulation(booleanInputs, gates, outputs);
            Map<String, Boolean> faultFreeOutputs = new HashMap<>(simulator.getValues());

            System.out.printf("Test Vector: %s | Fault-Free Outputs: %s\n", testVector, faultFreeOutputs);

            // Faulty simulation for each pass
            for (int pass = 0; pass < Math.ceil((double) faults.size() / faultsPerPass); pass++) {
                System.out.printf("Faulty Outputs (Pass %d):\n", pass + 1);

                for (int i = 0; i < faultsPerPass; i++) {
                    int faultIndex = pass * faultsPerPass + i;
                    if (faultIndex >= faults.size()) break;

                    String fault = faults.get(faultIndex);
                    String faultNode = fault.split("-")[0];
                    boolean faultValue = fault.endsWith("SA1"); // SA1 is true; SA0 is false

                    // Inject the fault
                    Map<String, Boolean> faultyInputs = new HashMap<>(booleanInputs);
                    faultyInputs.put(faultNode, faultValue);

                    // Run simulation with fault
                    simulator.runSimulation(faultyInputs, gates, outputs);
                    Map<String, Boolean> faultyOutputs = new HashMap<>(simulator.getValues());

                    System.out.printf("Fault: %s | Faulty Outputs: %s\n", fault, faultyOutputs);
                }
            }
        }
    }
}
