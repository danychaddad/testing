import java.util.*;

public class FaultInjector {
    private final Circuit circuit;

    FaultInjector(Circuit circuit) {
        this.circuit = circuit;
    }

    Map<String, Boolean> injectStuckAtFault(Map<String, Boolean> originalInputs, String nodeName, boolean stuckAtValue) {
        Map<String, Boolean> faultyInputs = new HashMap<>(originalInputs);
        if (faultyInputs.containsKey(nodeName)) {
            faultyInputs.put(nodeName, stuckAtValue);
        }
        return faultyInputs;
    }
}