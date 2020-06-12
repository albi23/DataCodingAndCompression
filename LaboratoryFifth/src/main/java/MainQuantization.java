import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;


/**
 * @Author Albert Piekielny
 */
public class MainQuantization {

    /**
     * Usage java MainQuantization <inputFile.tga> <outputFileName> colors_number
     * colors numbers is between 0 and 24
     */
    public static void main(String... args) throws IOException {

        if (args.length != 3) {
            System.err.println("Usage: java Main MainQuantization.tga <inputFile.tga> <outputFileName> colors_number\n" +
                    "      colors_number is between 0 and 24");
            System.exit(1);
        }
        if (!isCorrectExtension(args[0]))
            throw new IllegalArgumentException("Incorrect File Extension. Required: " + Arrays.toString(new String[]{"tga", "TGA",
                    "targa", "TARGA"}));
        final int colors = Integer.parseInt(args[2]);
        if (colors < 0 || colors > 24) throw new IllegalArgumentException("colors number is between 0 and 24");
        TruevisionTargaImageEncoder ttie = new TruevisionTargaImageEncoder(args[0], args[1], colors);
        ttie.photoQuantizationProcess();
    }

    public static boolean isCorrectExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("").equalsIgnoreCase("tga");
    }
}
