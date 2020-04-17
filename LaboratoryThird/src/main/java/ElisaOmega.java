import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ElisaOmega implements EliasCodes {

    public String encode(int num) {
        List<String> result = new ArrayList<>(4);
        result.add("0");

        while (num > 1) {
            final String binaryString = Integer.toBinaryString(num);
            result.add(binaryString);
            num = binaryString.length() - 1;
        }
        return result.stream()
                .collect(StringBuilder::new, (sb, c) -> sb.insert(0, c), (b1, b2) -> b1.insert(0, b2))
                .toString();
    }

    public Stack<Integer> decode(String code) {
        final Stack<Integer> response = new Stack<>();
        int n = 1;
        while (code.length() > 0) {
            if (code.charAt(0) == '0') {
                code = code.substring(1);
                response.push(n);
                n = 1;
            } else {
                int slice = Math.min(n + 1, code.length());
                final String tmp = code.substring(0, slice);
                code = code.substring(slice);
                n = Integer.parseInt(tmp, 2);
            }
        }
        return response;
    }
}
