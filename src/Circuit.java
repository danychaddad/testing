import java.util.*;

public class Circuit {
    private final Map<String, Boolean> inputs;
    private final Map<String, Gate> gates;
    private final List<String> outputs;

    Circuit(Map<String, Boolean> inputs, Map<String, Gate> gates, List<String> outputs) {
        this.inputs = inputs;
        this.gates = gates;
        this.outputs = outputs;
    }

    public Map<String, Boolean> getInputs() {
        return inputs;
    }

    public Map<String, Gate> getGates() {
        return gates;
    }

    public List<String> getOutputs() {
        return outputs;
    }

    public int getFaultSites() {
        int numberOfPIs = getInputs().size();
        int numberOfGates = getGates().size();
        int numberOfFanouts = getNumberOfFanouts(getGates());
        return numberOfFanouts + numberOfGates + numberOfPIs;
    }

    private int getNumberOfFanouts(Map<String, Gate> gates) {
        int numberOfFanouts = 0;
        Map<String, Integer> inputs = new HashMap<>();
        for (String gateId : gates.keySet()) {
            List<String> gateInputs = gates.get(gateId).getInputs();
            for (String input : gateInputs) {
                inputs.put(input, inputs.getOrDefault(input, 0) + 1);
            }
        }
        Set<String> inputKeys = inputs.keySet();
        for (String gateId : inputKeys) {
            if (inputs.get(gateId) == 1) {
                continue;
            } else {
                numberOfFanouts += inputs.get(gateId);
            }
        }
        return numberOfFanouts;
    }
}

