import java.io.IOException;

public class MainTGA {

    /**
     * Usage java Main photo.tga
     */
    public static void main(String ...args) throws IOException {

        final String photoInputPath = args[0];
        final TGAImageDataCollector tgaImageDataCollector = new TGAImageDataCollector();
        tgaImageDataCollector.getBufferedImage(args[0]);
        tgaImageDataCollector.printFileEntropy();
        tgaImageDataCollector.printColorsEntropy();
        tgaImageDataCollector.prediction();
    }


}

