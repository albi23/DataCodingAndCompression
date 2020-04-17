import org.apache.commons.lang3.StringUtils;

import java.util.Stack;

public class EliasGamma implements EliasCodes {


    public String encode(int num) {
        String response = "";
        if (num > 0) {
            int tmp = (int) (Math.log(num) / Math.log(2));
            final String binaryString = Integer.toBinaryString(num);
            response = StringUtils.leftPad(response, tmp, '0').concat(binaryString);
        }
        return response;
    }

    public Stack<Integer> decode(String code) {
        final Stack<Integer> response = new Stack<>();
        while (code.length() > 0) {
            final int i = code.indexOf("1");
            if (i < 0) break;
            code = code.substring(i);
            response.add(Integer.parseInt(code.substring(0, i + 1), 2));
            code = code.substring(i + 1);
        }
        return response;
    }
}
