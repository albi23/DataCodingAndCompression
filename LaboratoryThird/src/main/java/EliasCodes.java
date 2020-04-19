import java.util.Stack;

public interface EliasCodes {

    default  EliasCodes getInstance(int state) {
        switch (state) {
            case 1:
                return new EliasDelta();
            case 2:
                return new EliasFibonacci();
            case 3:
                return new EliasGamma();
            default:
                return new ElisaOmega();
        }
    }

    public Stack<Integer> decode(String code);

    public String encode(int num);
}
