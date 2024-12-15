import java.util.*;

enum GateType {
    AND, OR, NOT, NAND, NOR, XOR, BUFF, XNOR
}

class Gate {
    private final String output;
    private final GateType type;
    private final List<String> inputs;

    Gate(String output, GateType type, List<String> inputs) {
        this.output = output;
        this.type = type;
        this.inputs = inputs;
    }

    public String getOutput() {
        return output;
    }

    public GateType getType() {
        return type;
    }

    public List<String> getInputs() {
        return inputs;
    }
}
