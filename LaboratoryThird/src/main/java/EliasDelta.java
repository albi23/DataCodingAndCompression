import org.apache.commons.lang3.StringUtils;

import java.util.Stack;

public class EliasDelta implements EliasCodes {


    public Stack<Integer> decode(String code) {
        final Stack<Integer> response = new Stack<>();
        while (code.length() > 0) {
            final int i = code.indexOf('1');
            if (i == -1) break;
            code = code.substring(i);
            final int n = Integer.parseInt(code.substring(0, i + 1), 2) - 1;
            code = code.substring(i + 1);
            response.push(Integer.parseInt("1".concat(code.substring(0, n)), 2));
            code = code.substring(n);
        }
        return response;
    }


    public String encode(int num) {
        String code = "";
        if (num > 0) {
            int tmp = (int) (Math.log(num) / Math.log(2));
            int pad = (int) (Math.log(tmp + 1) / Math.log(2));
            code = StringUtils.leftPad(code, pad, '0')
                    .concat(Integer.toBinaryString(tmp + 1))
                    .concat(Integer.toBinaryString(num).substring(1));
        }
        return code;
    }
}
