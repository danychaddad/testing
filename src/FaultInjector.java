import java.util.*;

public class FaultInjector {
    private final Circuit circuit;

    FaultInjector(Circuit circuit) {
        this.circuit = circuit;
    }

    Map<String, Boolean> injectStuckAtFault(Map<String, Boolean> originalInputs, String nodeName, boolean stuckAtValue) {
        Map<String, Boolean> faultyInputs = new HashMap<>(originalInputs);

        // Check if the node is an input
        if (faultyInputs.containsKey(nodeName)) {
            faultyInputs.put(nodeName, stuckAtValue);
        } else {
            // Check if the node is an output of a gate
            for (Gate gate : circuit.getGates().values()) {
                if (gate.getOutput().equals(nodeName)) {
                    // Inject the fault directly into the gate's output
                    faultyInputs.put(nodeName, stuckAtValue);
                    break;
                }
            }
        }

        return faultyInputs;
    }
}