import core.FileProcessing;
import decode.AdaptiveDecoder;
import encode.AdaptiveEncoder;

import java.io.IOException;

public class CoredMain {
    public static void main(String[] args) throws IOException, IllegalArgumentException {

        if (args.length != 3) {
            System.err.println("Usage: java  ComprisionMain --encode inputFile outputFile");
            System.err.println("Usage: java  ComprisionMain --decode inputFile outputFile");
            System.exit(1);
        }
        FileProcessing fileProcessingI;
        switch (args[0]) {
            case "--encode":
                fileProcessingI = new AdaptiveEncoder(args[1], args[2]);
                break;
            case "--decode":
                fileProcessingI = new AdaptiveDecoder(args[1], args[2]);
                break;
            default:
                throw new UnsupportedOperationException(String.format("No support for %s option", args[0]));
        }
        fileProcessingI.startProcessingFile();
//        File inputFile = new File(args[0]);
//        File outputFile = new File(args[1]);
//
//        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
//             BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outputFile)))) {
//            compress(in, out);
//        }
    }


//    static void compress(InputStream in, BitOutputStream out) throws IOException {
//        int[] initFreqs = new int[257];
//         Arrays.fill(initFreqs,1);
//        SimpleFrequencyTable freqs = new SimpleFrequencyTable(initFreqs);
//        ArithmeticEncoder enc = new ArithmeticEncoder(32, out);
//        while (true) {
//            // Read and encode one byte
//            int symbol = in.read();
//            if (symbol == -1)
//                break;
//            enc.write(freqs, symbol);
//            freqs.increment(symbol);
//        }
//        enc.write(freqs, 256);  // EOF
//        enc.finish();  // Flush remaining code bits
//    }

/*    private static class BitOutputStream implements AutoCloseable {

        private OutputStream output;
        private int currentByte;
        private int numBitsFilled;

        public BitOutputStream(OutputStream out) {
            output = Objects.requireNonNull(out);
            currentByte = 0;
            numBitsFilled = 0;
        }

        public void write(int b) throws IOException {
            if (b != 0 && b != 1)
                throw new IllegalArgumentException("Argument must be 0 or 1");
            currentByte = (currentByte << 1) | b;
            numBitsFilled++;
            if (numBitsFilled == 8) {
                output.write(currentByte);
                currentByte = 0;
                numBitsFilled = 0;
            }
        }

        public void close() throws IOException {
            while (numBitsFilled != 0)
                write(0);
            output.close();
        }
    }
    private  static class SimpleFrequencyTable{

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
                total +=x;
            }
            cumulative = null;
        }

        public void increment(int symbol) {
            if (frequencies[symbol] == Integer.MAX_VALUE)
                throw new ArithmeticException("Arithmetic overflow");
            total = (total +  1);
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
                // This arithmetic should not throw an exception, because invariants are being maintained
                // elsewhere in the data structure. This implementation is just a defensive measure.
                sum = (frequencies[i] + sum);
                cumulative[i + 1] = sum;
            }
            if (sum != total)
                throw new AssertionError();
        }

    }
    public static class ArithmeticEncoder{

        protected final int numStateBits;
        protected final long fullRange;
        protected final long halfRange;
        protected final long quarterRange;
        protected final long minimumRange;
        protected final long maximumTotal;
        protected final long stateMask;
        protected long low;
        protected long high;

        private BitOutputStream output;
        private int numUnderflow;

        public ArithmeticEncoder(int numBits, BitOutputStream out) {
            if (numBits < 1 || numBits > 62)
                throw new IllegalArgumentException("State size out of range");
            numStateBits = numBits;
            output = Objects.requireNonNull(out);

            fullRange = 1L << numStateBits;
            halfRange = fullRange >>> 1;  // Non-zero
            quarterRange = halfRange >>> 1;  // Can be zero
            minimumRange = quarterRange + 2;  // At least 2
            maximumTotal = Math.min(Long.MAX_VALUE / fullRange, minimumRange);
            stateMask = fullRange - 1;

            low = 0;
            high = stateMask;

            numUnderflow = 0;
        }

        protected void update(SimpleFrequencyTable freqs, int symbol) throws IOException {
            // State check
            long range = high - low + 1;
            long total = freqs.getTotal();
            long symLow = freqs.getLow(symbol);
            long symHigh = freqs.getHigh(symbol);

            long newLow  = low + symLow  * range / total;
            long newHigh = low + symHigh * range / total - 1;
            low = newLow;
            high = newHigh;

            while (((low ^ high) & halfRange) == 0) {
                shift();
                low  = ((low  << 1) & stateMask);
                high = ((high << 1) & stateMask) | 1;
            }
            while ((low & ~high & quarterRange) != 0) {
                underflow();
                low = (low << 1) ^ halfRange;
                high = ((high ^ halfRange) << 1) | halfRange | 1;
            }
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
    }*/


}