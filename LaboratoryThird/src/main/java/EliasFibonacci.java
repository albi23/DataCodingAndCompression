import java.util.Stack;

public class EliasFibonacci implements EliasCodes {

    private static final long[] fibLong = new long[91];

    static {
        fibLong[0] = 1;
        fibLong[1] = 2;
        for (int i = 2; i < fibLong.length; i++)
            fibLong[i] = fibLong[i - 1] + fibLong[i - 2];
    }

    public final int getLargerOrEqualFibGum(final int n) {
        int i;
        for (i = 2; fibLong[i - 1] <= n; i++) ;
        return i - 2;
    }

    @Override
    public String encode(int num) {
        int i = getLargerOrEqualFibGum(num);
        StringBuilder code = new StringBuilder(i + 2);
        while (num > 0) {
            code.insert(0, "1");
            num = num - (int) fibLong[i--];
            for (; i >= 0 && fibLong[i] > num; i--)
                code.insert(0, '0');
        }
        return code.append('1').toString();
    }

    @Override
    public Stack<Integer> decode(String code) {
        final Stack<Integer> integers = new Stack<>();
        final String[] codes = code.split("11");
        for (int i = 0; i < codes.length - 1; i++) {
            codes[i] = codes[i].concat("11");
            int result = 0;
            for (int j = 0; j < codes[i].length() - 1; j++) {
                if (codes[i].charAt(j) == '1')
                    result += (int) fibLong[j];
            }
            integers.push(result);
        }
        return integers;
    }
}