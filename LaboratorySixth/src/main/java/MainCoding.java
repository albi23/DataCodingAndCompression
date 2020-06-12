import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;

/**
 * @Author Albert Piekielny
 */
public class MainCoding {

    /**
     * Usage java MainCoding flag <inputFile.tga>  quantizationBitLength
     * flag :  -decode/-encode
     * quantizationBitLength number is between 1 and 7
     */
    public static void main(String... args) throws IOException {

        if (args.length == 3 && TGAUtils.isCorrectExtension(args[1])) {
            if (args[0].equals("-encode")) {
                final short quantizationBitAmount = TGAUtils.validateQuantizationBitAmount(Short.parseShort(args[2]));
                new TGAEncoder().encode(args[1], quantizationBitAmount);
            } else if (args[0].equals("-decode")) {
                throw new NotImplementedException();
            } else {
                printErrInfo();
            }
        } else {
            printErrInfo();
        }

    }

    private static void printErrInfo() {
        System.err.println("Usage:  java MainCoding flag <inputFile.tga> <outputFileName> k, 0<k<8");
        System.err.println("flag :  -decode/-encode");
        System.err.println("Const k specify quantizer bits length.");
        System.exit(1);
    }
}
