import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * @Author Albert Piekielny
 */
public final class TGAUtils {

    public static final int TGA_IMAGE_FOOTER = 26;
    public static final int TGA_IMAGE_HEADER = 18;

    private TGAUtils() {
    }

    public static <T extends Number> T validateQuantizationBitAmount(T quantizationBit) {
        if (quantizationBit.doubleValue() <= 0 || quantizationBit.doubleValue() > 7) throw new IllegalArgumentException(
                String.format("Bits amount for quantization is [1,7]. Current value %d", quantizationBit.intValue()));
        return quantizationBit;
    }

    public static boolean isCorrectExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("").equalsIgnoreCase("tga");
    }

    public static byte[] readFile(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    public static int byteToInt(byte b) {
        return b & 0xFF;
    }


    public static int getTgaImageWidth(byte[] tgaBuff) {
        return byteToInt(tgaBuff[12]) + (byteToInt(tgaBuff[13]) << 8);
    }

    public static int getTgaImageHeight(byte[] tgaBuff) {
        return byteToInt(tgaBuff[14]) + (byteToInt(tgaBuff[15]) << 8);
    }

    public static Pixel[] createPixelArray(byte[] tgaBuff) {
        int n = getTgaImageWidth(tgaBuff) * getTgaImageHeight(tgaBuff);
        Pixel[] imagePixels = new Pixel[n];

        if (tgaBuff[2] == 0x02 && tgaBuff[16] == 0x20) { // uncompressed RGBA
            throw new UnsupportedOperationException("Image has RGBA color definition!");
        } else if (tgaBuff[2] == 0x02 && tgaBuff[16] == 0x18) {  // uncompressed RGB
            for (int i = 0; i < n; i++) {
                int b = tgaBuff[i * 3] & 0xFF;
                int g = tgaBuff[i * 3 + 1] & 0xFF;
                int r = tgaBuff[i * 3 + 2] & 0xFF;
                imagePixels[i] = new Pixel(r, g, b);
            }
        } else {
            throw new UnsupportedOperationException("Image is compressed!");
        }
        return imagePixels;
    }

}
