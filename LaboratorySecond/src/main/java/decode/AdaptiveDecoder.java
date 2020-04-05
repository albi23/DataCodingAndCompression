package decode;

import core.FileProcessing;
import core.SimpleFrequencyTable;
import utils.Utility;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

public class AdaptiveDecoder implements FileProcessing {

    private final String outFile;
    private final String inputFile;

    public AdaptiveDecoder(String inputFilePath, String outputFilePath) {
        this.inputFile = Objects.requireNonNull(inputFilePath);
        if (outputFilePath == null || outputFilePath.length() == 0)
            outputFilePath = System.getProperty("user.dir").concat("/encodedFile.bin");

        this.outFile = outputFilePath;
    }

    @Override
    public void startProcessingFile() throws IOException {
        Utility.isCorrectFilePath(inputFile);
        try (BitInputStream in = new BitInputStream(new BufferedInputStream(new FileInputStream(inputFile)));
             OutputStream out = new BufferedOutputStream(new FileOutputStream(outFile))) {
            decompress(in, out);
        }
    }

    private void decompress(BitInputStream in, OutputStream out) throws IOException {
        int[] initFreqs = new int[257];
        Arrays.fill(initFreqs, 1);
        SimpleFrequencyTable freqs = new SimpleFrequencyTable(initFreqs);
        ArithmeticDecoder dec = new ArithmeticDecoder(32, in);
        while (true) {
            int symbol = dec.read(freqs);
            if (symbol == 256)  // EOF symbol
                break;
            out.write(symbol);
            freqs.increment(symbol);
        }
    }
}
