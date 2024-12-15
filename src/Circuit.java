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
}

