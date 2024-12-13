import java.util.List;

enum GateType {
    AND, OR, NOT, NAND, NOR, XOR
}

class Gate {
    String output;
    GateType type;
    List<String> inputs;

    Gate(String output, GateType type, List<String> inputs) {
        this.output = output;
        this.type = type;
        this.inputs = inputs;
    }
}
