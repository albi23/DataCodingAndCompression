import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class TGAImageDataCollector extends TGAImageReader {
    private final ThreeArgsFunction<Integer>[] predictions = new ThreeArgsFunction[7];
    private final ImageStats imageStats;

    public TGAImageDataCollector(ImageStats imageStats) {
        this.imageStats = Objects.requireNonNull(imageStats);
        initPrediction();
    }

    public TGAImageDataCollector() {
        this.imageStats = new ImageStats();
        initPrediction();
    }

    private void initPrediction() {
        predictions[0] = (x, y, z) -> x;
        predictions[1] = (x, y, z) -> y;
        predictions[2] = (x, y, z) -> z;
        predictions[3] = (x, y, z) -> (x + y - z);
        predictions[4] = (x, y, z) -> (x + (y - z) / 2);
        predictions[5] = (x, y, z) -> (y + (x - z) / 2);
        predictions[6] = (x, y, z) -> ((x + y) / 2);
    }

    private static int mod256(int x) {
        int value = x % 256;
        return value < 0 ? value & 0xFF : value;
    }

    @Override
    public BufferedImage getBufferedImage(String filepath) throws IOException {
        final byte[] bytes = readFileAsBytes(filepath);
        return decode(bytes);
    }

    @Override
    protected int[] createArrayWithPixels(int width, int height, byte[] buff) {
        int n = width * height;
        this.imageStats.setImageWidth(width);
        this.imageStats.initPixelsArray(width, height);

        int[] pixels = new int[n];
        int idx = 0;
        n -= TGA_IMAGE_FOOTER; // Skip footer data
        if (n < 0)
            throw new IllegalArgumentException(String.format("Image file size is to small, min  is %d", TGA_IMAGE_FOOTER + TGA_IMAGE_HEADER));

        final boolean isUncompressed = buff[2] == 0x02;
        if (isUncompressed && buff[16] == 0x20) { // uncompressed RGBA
            while (n > 0) {
                int b = read(buff), g = read(buff), r = read(buff), a = read(buff);
                this.increaseOccurrences(b, g, r);
                this.addPixel(b, g, r);
                pixels[idx++] = (a << 24) | (r << 16) | (g << 8) | b;
                n -= 1;
            }
        } else if (isUncompressed && buff[16] == 0x18) {  // uncompressed RGB
            while (n > 0) {
                int b = read(buff), g = read(buff), r = read(buff), a = 255;
                this.increaseOccurrences(b, g, r);
                this.addPixel(b, g, r);
                pixels[idx++] = (a << 24) | (r << 16) | (g << 8) | b;
                n -= 1;
            }
        } else {
            throw new UnsupportedOperationException("Image is compressed!");
        }
        return pixels;
    }

    private void increaseOccurrences(int b, int g, int r) {
        this.imageStats.increaseBlue(b);
        this.imageStats.increaseGreen(g);
        this.imageStats.increaseRed(r);
        this.imageStats.increaseSigns(r, g, b);
    }

    private void addPixel(int b, int g, int r){
        this.imageStats.storePixel(r, g, b);
    }

    public void printColorsEntropy() {
        System.out.println(String.format("%-18s", "File entropy") + countEntropy(this.imageStats.getAllSignOccurrences(), this.imageStats.getSigns()));
        System.out.println(String.format("%-18s", "Red entropy") + countEntropy(this.imageStats.getRedOccurrences(), this.imageStats.getRed()));
        System.out.println(String.format("%-18s", "Blue entropy") + countEntropy(this.imageStats.getBlueOccurrences(), this.imageStats.getBlue()));
        System.out.println(String.format("%-18s", "Green entropy") + countEntropy(this.imageStats.getGreenOccurrences(), this.imageStats.getGreen()));
    }

    /**
     * Prediction visualisation
     * | | | | | |
     * | |C|B|D| |
     * | |A|X| | |
     * | | | | | |
     */
    public void prediction() {

        final ImageStats.Pixel[][] pixels = this.imageStats.getPixels();
        for (ThreeArgsFunction<Integer> func : predictions) {
            this.imageStats.initRGBArrays();
            for (int i = 0; i < pixels.length; i++) {
                for (int j = 0; j < pixels[0].length; j++) {
                    final ImageStats.Pixel pixelX = pixels[i][j];

                    final ImageStats.Pixel pixelA = getAdjacentPixels(i - 1, j, pixels);
                    final ImageStats.Pixel pixelC = getAdjacentPixels(i - 1, j - 1, pixels);
                    final ImageStats.Pixel pixelB = getAdjacentPixels(i - 1, j - 1, pixels);

                    int predictedRed = mod256(pixelX.r - func.apply(pixelC.r, pixelB.r, pixelA.r));
                    int predictedGreen = mod256(pixelX.g - func.apply(pixelC.g, pixelB.g, pixelA.g));
                    int predictedBlue = mod256(pixelX.b - func.apply(pixelC.b, pixelB.b, pixelA.b));

                    this.increaseOccurrences(predictedBlue, predictedGreen, predictedRed);

                }
            }
            System.out.println("\n");
            printColorsEntropy();
        }
    }

    private ImageStats.Pixel getAdjacentPixels(int i, int j, ImageStats.Pixel[][] pixels) {
        return (i >= 0 && i <= pixels.length && j >= 0 && j <= pixels[0].length) ? pixels[i][j] : new ImageStats.Pixel(0, 0, 0);
    }

    public static double countEntropy(long allSymbolsOccurrences, int[] symbolsData) {
        double entropy = 0.0D;
        double logFromAll = log2(allSymbolsOccurrences);
        for (int data : symbolsData) {
            if (data <= 0) continue;
            entropy += data * (logFromAll - log2(data));
        }
        return entropy / allSymbolsOccurrences;
    }

    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

}
