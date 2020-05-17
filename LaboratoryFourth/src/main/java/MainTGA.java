import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Optional;

public class MainTGA {

    /**
     * Usage java Main photo.tga
     */
    public static void main(String... args) throws IOException {

        final String filePath = args[0];
        if(!isCorrectExtension(filePath))
            throw new IllegalArgumentException(String.format("Incorrect file extension: %s", filePath));
        final TGAImageDataCollector tgaImageDataCollector = new TGAImageDataCollector();
         BufferedImage bufferedImage = tgaImageDataCollector.getBufferedImage(filePath);


//        final byte[] buffer = Files.readAllBytes(Paths.get(filePath));
//        final int[] pixels = TGAReader.read(buffer, TGAReader.ARGB);
//        final int width = TGAReader.getWidth(buffer);
//        final int height = TGAReader.getHeight(buffer);
//        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB) {{
//            setRGB(0, 0, width, height, pixels, 0, width);
//        }};
        /*final BufferedImage */
//        bufferedImage = ImageIO.read(new File(filePath));
//        ImageIO.write(bufferedImage, "TGA", new File("image1.tga"));
//        System.exit(1);
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

