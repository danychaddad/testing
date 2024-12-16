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
            // Check if the node is an output of a gate or a fanout wire
            for (Gate gate : circuit.getGates().values()) {
                if (gate.getOutput().equals(nodeName)) {
                    // Inject the fault directly into the gate's output
                    faultyInputs.put(nodeName, stuckAtValue);
                    break;
                } else if (gate.getInputs().contains(nodeName)) {
                    // Inject the fault into the fanout wire independently
                    faultyInputs.put(nodeName, stuckAtValue);
                }
            }
        }

        return faultyInputs;
    }
}