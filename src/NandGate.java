import java.util.List;

public class NandGate extends BooleanGate {

    public NandGate(List<Boolean> inputs) {
        super(inputs);
    }

    @Override
    protected void eval() {
        final boolean[] result = {true};
        inputs.forEach(input -> result[0] = result[0] && input);
        output = !result[0];
    }
}
