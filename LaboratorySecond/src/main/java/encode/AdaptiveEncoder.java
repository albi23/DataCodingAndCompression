package encode;

import core.FileProcessing;
import core.SimpleFrequencyTable;
import utils.Utility;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;

public class AdaptiveEncoder implements FileProcessing {

    private String inputFile;
    private String outFile;

    public AdaptiveEncoder(String inputFilePath, String outputFilePath) {
        this.inputFile = Objects.requireNonNull(inputFilePath);
        if (outputFilePath == null || outputFilePath.length() == 0)
            outputFilePath = System.getProperty("user.dir").concat("/encodedFile.bin");

        this.outFile = outputFilePath;
    }

    private void compress(InputStream in, BitOutputStream out) throws IOException {
        int[] initFreqs = new int[257];
        Arrays.fill(initFreqs, 1);
        SimpleFrequencyTable freqs = new SimpleFrequencyTable(initFreqs);
        ArithmeticEncoder enc = new ArithmeticEncoder(32, out);
        while (true) {
            int symbol = in.read();
            if (symbol == -1)
                break;
            enc.write(freqs, symbol);
            freqs.increment(symbol);
        }
        enc.write(freqs, 256);
        enc.finish();
    }

    @Override
    public void startProcessingFile() throws IOException {
        Utility.isCorrectFilePath(inputFile);
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputFile));
             BitOutputStream out = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(outFile)))) {
            this.compress(in, out);
        }
    }
}
