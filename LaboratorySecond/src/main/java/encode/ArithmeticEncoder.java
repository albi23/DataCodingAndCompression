package encode;

import core.BaseArithmeticCoder;
import core.SimpleFrequencyTable;

import java.io.IOException;
import java.util.Objects;

public class ArithmeticEncoder extends BaseArithmeticCoder {

    private BitOutputStream output;
    private int numUnderflow;

    public ArithmeticEncoder(int numBits, BitOutputStream out) {
        super(numBits);
        output = Objects.requireNonNull(out);
        numUnderflow = 0;
    }


    public void write(SimpleFrequencyTable freqs, int symbol) throws IOException {
        update(freqs, symbol);
    }

    public void finish() throws IOException {
        output.write(1);
    }


    protected void shift() throws IOException {
        int bit = (int)(low >>> (numStateBits - 1));
        output.write(bit);

        // Write out the saved underflow bits
        for (; numUnderflow > 0; numUnderflow--)
            output.write(bit ^ 1);
    }


    protected void underflow() {
        if (numUnderflow == Integer.MAX_VALUE)
            throw new ArithmeticException("Maximum underflow reached");
        numUnderflow++;
    }
}
