import java.util.List;

public class Main {
    public static void main(String[] args) {
        NandGate nand = new NandGate(List.of(false, false, false));
        System.out.println(nand.output);
    }
}
