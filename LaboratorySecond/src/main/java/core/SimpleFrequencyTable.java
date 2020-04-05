package core;

import java.util.Objects;

public class SimpleFrequencyTable {

    private int[] frequencies;
    private int[] cumulative;
    private int total;

    public SimpleFrequencyTable(int[] freqs) {
        Objects.requireNonNull(freqs);
        frequencies = freqs.clone();  // Make copy
        total = 0;
        for (int x : frequencies) {
            if (x < 0)
                throw new IllegalArgumentException("Negative frequency");
            total += x;
        }
        cumulative = null;
    }

    public void increment(int symbol) {
        if (frequencies[symbol] == Integer.MAX_VALUE)
            throw new ArithmeticException("Arithmetic overflow");
        total = (total + 1);
        frequencies[symbol]++;
        cumulative = null;
    }

    public int getSymbolLimit() {
        return frequencies.length;
    }

    public int getTotal() {
        return total;
    }


    public int getLow(int symbol) {
        if (cumulative == null)
            initCumulative();
        return cumulative[symbol];
    }

    public int getHigh(int symbol) {
        if (cumulative == null)
            initCumulative();
        return cumulative[symbol + 1];
    }

    private void initCumulative() {
        cumulative = new int[frequencies.length + 1];
        int sum = 0;
        for (int i = 0; i < frequencies.length; i++) {
            sum = (frequencies[i] + sum);
            cumulative[i + 1] = sum;
        }
        if (sum != total)
            throw new AssertionError();
    }
}
