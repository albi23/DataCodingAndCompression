import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * @Author Albert Piekielny
 *
 */
public class TruevisionTargaImageEncoder {
    private static final int TGA_IMAGE_FOOTER = 26;
    private static final int TGA_IMAGE_HEADER = 18;
    private final byte[] tgaImageBuff;
    private final int k;
    private final String outputPath;
    private Pixel[] imagePixels;

    public TruevisionTargaImageEncoder(String inputFilePath, String outFilePath, int k) throws IOException {
        if (k <= 0) throw new IllegalArgumentException("Negative k value :" + k);
        this.k = k;
        this.tgaImageBuff = readFile(Objects.requireNonNull(inputFilePath));
        this.outputPath = Objects.requireNonNull(outFilePath);
    }

    private static int byteToInt(byte b) {
        return b & 0xFF;
    }

    private static byte[] readFile(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    private static int getTgaImageWidth(byte[] imageBuff) {
        return byteToInt(imageBuff[12]) + (byteToInt(imageBuff[13]) << 8);
    }

    private static int getTgaImageHeight(byte[] imageBuff) {
        return byteToInt(imageBuff[14]) + (byteToInt(imageBuff[15]) << 8);
    }

    public void photoQuantizationProcess() throws IOException {
        final int imgWidth = getTgaImageWidth(this.tgaImageBuff);
        final int imgHeight = getTgaImageHeight(this.tgaImageBuff);
        this.createPixelsArray(imgWidth, imgHeight, this.tgaImageBuff);
        final Pixel[] vectorizedPixelArray = this.generateVectorizedPixelArray();
        Files.write(Paths.get(outputPath), this.pixelsToBytesArray(vectorizedPixelArray));
        final double mseRate = calculateMSE(vectorizedPixelArray);
        System.out.println("MSE rate : "+ mseRate);
        System.out.println("SNR rate : "+calculateSNR(mseRate));
    }

    protected void createPixelsArray(int width, int height, byte[] buff) {
        int n = width * height;
        this.imagePixels = new Pixel[n];

        if (buff[2] == 0x02 && buff[16] == 0x20) { // uncompressed RGBA
            throw new UnsupportedOperationException("Image has RGBA color definition!");
        } else if (buff[2] == 0x02 && buff[16] == 0x18) {  // uncompressed RGB
            for (int i = 0; i < n; i++) {
                int b = buff[i * 3] & 0xFF;
                int g = buff[i * 3 + 1] & 0xFF;
                int r = buff[i * 3 + 2] & 0xFF;
                this.imagePixels[i] = new Pixel(r, g, b);
            }
        } else {
            throw new UnsupportedOperationException("Image is compressed!");
        }
    }

    private Pixel[] generateVectorizedPixelArray() {
        final VectorQuantization vectorQuantization = new VectorQuantization(this.imagePixels, k);
        vectorQuantization.initDictionary();
        return vectorQuantization.createQuantizedPixelSet();
    }

    private byte[] pixelsToBytesArray(Pixel[] pixels) {
        final byte[] bytes = new byte[this.tgaImageBuff.length];
        System.arraycopy(this.tgaImageBuff, 0, bytes, 0, TGA_IMAGE_HEADER); // copy header
        System.arraycopy(this.tgaImageBuff, bytes.length - TGA_IMAGE_FOOTER - 1, bytes, bytes.length - TGA_IMAGE_FOOTER - 1, TGA_IMAGE_FOOTER); // copy footer

        for (int i = 0, idx = TGA_IMAGE_HEADER; i < pixels.length; i++) {
            bytes[idx++] = (byte) ((int) (pixels[i].getBlue()) & 0xFF);
            bytes[idx++] = (byte) ((int) (pixels[i].getGreen()) & 0xFF);
            bytes[idx++] = (byte) ((int) (pixels[i].getRed()) & 0xFF);
        }
        return bytes;
    }

    private double calculateMSE(Pixel[] pixels) {
        return (1.0 / pixels.length) * IntStream.range(0, this.imagePixels.length - 1)
                .mapToObj(i -> Math.pow(this.imagePixels[i].getDistanceToPixel(pixels[i]), 2)).parallel()
                .reduce(0.0, Double::sum);
    }

    private double calculateSNR(double mseRate) {
        return ((1.0 / this.imagePixels.length) * IntStream.range(0, this.imagePixels.length - 1)
                .mapToObj(i -> this.imagePixels[i].squaredVectorLength()).parallel()
                .reduce(0.0, Double::sum)) / mseRate;
    }

}
