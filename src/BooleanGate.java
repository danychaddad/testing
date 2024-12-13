import java.util.List;

public abstract class BooleanGate {
    protected List<Boolean> inputs;
    protected boolean output;

    public BooleanGate(List<Boolean> inputs) {
        this.inputs = inputs;
        eval();
    }

    public List<Boolean> getInputs() {
        return inputs;
    }

    public void setInputs(List<Boolean> inputs) {
        this.inputs = inputs;
        eval();
    }

    public boolean getOutput() {
        return output;
    }

    protected abstract void eval();
}
