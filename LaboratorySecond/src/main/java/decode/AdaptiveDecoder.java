package decode;

import core.FileProcessing;
import core.ASCIISignFrequency;
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
            outputFilePath = System.getProperty("user.dir").concat("/decodedFile.txt");

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
        int[] initFreq = new int[257];
        Arrays.fill(initFreq, 1);
        ASCIISignFrequency freq = new ASCIISignFrequency(initFreq);
        ArithmeticDecoder dec = new ArithmeticDecoder(32, in);
        while (true) {
            int symbol = dec.read(freq);
            if (symbol == 256)  // EOF symbol
                break;
            out.write(symbol);
            freq.increment(symbol);
        }
    }
}
