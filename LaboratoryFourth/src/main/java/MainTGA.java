import java.io.IOException;
import java.util.Optional;

public class MainTGA {

    /**
     * Usage java Main photo.tga
     */
    public static void main(String... args) throws IOException {

        final String filePath = args[0];
        if (!isCorrectExtension(filePath))
            throw new IllegalArgumentException(String.format("Incorrect file extension: %s", filePath));
        final TGAImageDataCollector tgaImageDataCollector = new TGAImageDataCollector();
        tgaImageDataCollector.getBufferedImage(filePath);

        tgaImageDataCollector.printFileEntropy();
        tgaImageDataCollector.printColorsEntropy();
        tgaImageDataCollector.prediction();
    }

    public static boolean isCorrectExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("").equals("tga");
    }

}

