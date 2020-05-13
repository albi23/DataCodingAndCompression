import java.io.IOException;
import java.util.Optional;

public class MainQuantization {

    /**
     * Usage java Main MainQuantization.tga <inputFile.tga> <outputFileName> colors_number
     * colors numbers is between 0 and 24
     */
    public static void main(String... args) throws IOException {

        if (args.length != 3){
            System.err.println("Usage: java Main MainQuantization.tga <inputFile.tga> <outputFileName> colors_number\n" +
                    "      colors_number is between 0 and 24");
            System.exit(1);
        }
        if (isCorrectExtension(args[0])){
            TruevisionTargaImageEncoder ttie = new TruevisionTargaImageEncoder(args[0], Integer.parseInt(args[2]));
        }
    }

    public static boolean isCorrectExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("").equals("tga");
    }
}
