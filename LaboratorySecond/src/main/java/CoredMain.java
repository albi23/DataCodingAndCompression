import core.FileProcessing;
import decode.AdaptiveDecoder;
import encode.AdaptiveEncoder;

import java.io.IOException;

public class CoredMain {
    public static void main(String[] args) throws IOException, IllegalArgumentException {

        if (args.length != 3) {
            System.err.println("Usage: java  CoredMain --encode inputFile outputFile");
            System.err.println("Usage: java  CoredMain --decode inputFile outputFile");
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
    }
}