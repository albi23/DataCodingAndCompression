import java.util.Objects;
import java.util.function.BiFunction;

public class TGAImageDataCollector extends TGAImageReader {
    private final ThreeArgsFunction<Integer>[] func = new ThreeArgsFunction[8];
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
        func[0] = (x, y, z) -> x;
        func[1] = (x, y, z) -> y;
        func[2] = (x, y, z) -> z;
        func[3] = (x, y, z) -> (x + y - z);
        func[4] = (x, y, z) -> (x + (y - z) / 2);
        func[5] = (x, y, z) -> (y + (x - z) / 2);
        func[6] = (x, y, z) -> ((x + y) / 2);
        func[7] = (x, y, z) -> {
            final int max = Math.max(y, z);
            if (x >= max) return max;
            final int min = Math.min(y, z);
            return (x <= min) ? min : (z + y - x);
        };
    }

    private static int mod256(int x) {
        int value = x % 256;
        return value < 0 ? value & 0xFF : value;
    }

    @Override
    protected int[] createArrayWithPixels(int width, int height, byte[] buff) {
        int n = width * height;
        this.imageStats.setImageWidth(width);
        this.imageStats.initPixelsArray(width, height);
        int descriptor = buff[17] & 0xFF;
        BiFunction<Integer, Integer, Integer> layoutIdx = this.getDefiningLayoutFunction(descriptor, width, height);

        int[] pixels = new int[n];
        if (n < 0)
            throw new IllegalArgumentException(String.format("Image file size is to small, min  is %d", TGA_IMAGE_FOOTER + TGA_IMAGE_HEADER));

        final boolean isUncompressed = buff[2] == 0x02;
        if (isUncompressed && buff[16] == 0x20) { // uncompressed RGBA
            throw new UnsupportedOperationException("Image has RGBA color definition!");
        } else if (isUncompressed && buff[16] == 0x18) {  // uncompressed RGB
            for (int i = 0, offset ;  i < height; i++) {
                offset = 18 + 3 * width * i;
                for (int j = 0; j < width; j++) {
                    int index = offset + 3 * j;
                    int b = buff[index] & 0xFF;
                    int g = buff[index + 1] & 0xFF;
                    int r = buff[index + 2] & 0xFF;
                    int a = 0xFF;
                    this.increaseRGBOccurrences(b, g, r);
                    this.increaseAllSign(b, g, r);
                    this.addPixel(b, g, r);
                    pixels[layoutIdx.apply(i,j)] = (r << 16) | (g << 8) | (b << 0) | (a << 24);
                }
            }
        } else {
            throw new UnsupportedOperationException("Image is compressed!");
        }
        return pixels;
    }


    private void increaseRGBOccurrences(int b, int g, int r) {
        this.imageStats.increaseBlue(b);
        this.imageStats.increaseGreen(g);
        this.imageStats.increaseRed(r);
    }

    private void increaseAllSign(int b, int g, int r) {
        this.imageStats.increaseSigns(r, g, b);
    }

    private void addPixel(int b, int g, int r) {
        this.imageStats.storePixel(r, g, b);
        this.imageStats.increaseAllSignOccurrences(3);
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
        double[] entropyResults = new double[]{Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE};// red green blue all
        int[] bestFunctions = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,};// red green blue all

        for (int f_idx = 0; f_idx < func.length; f_idx++) {
            ThreeArgsFunction<Integer> func = this.func[f_idx];
            this.imageStats.initRGBArrays();
            this.imageStats.initSignArray();
            for (int i = 0; i < pixels.length; i++) {
                for (int j = 0; j < pixels[i].length; j++) {
                    final ImageStats.Pixel pixelX = pixels[i][j];
                    final ImageStats.Pixel pixelA = getAdjacentPixels(i - 1, j, pixels);
                    final ImageStats.Pixel pixelB = getAdjacentPixels(i, j - 1, pixels);
                    final ImageStats.Pixel pixelC = getAdjacentPixels(i - 1, j - 1, pixels);

                    int predictedRed = mod256(func.apply(pixelB.r, pixelA.r, pixelC.r) - pixelX.r);
                    int predictedGreen = mod256(func.apply(pixelB.g, pixelA.g, pixelC.g) - pixelX.g);
                    int predictedBlue = mod256(func.apply(pixelB.b, pixelA.b, pixelC.b) - pixelX.b);

                    this.increaseRGBOccurrences(predictedBlue, predictedGreen, predictedRed);
                    this.increaseAllSign(predictedBlue, predictedGreen, predictedRed);
                }
            }
            double[] current = new double[]{
                    countEntropy(this.imageStats.getRedOccurrences(), this.imageStats.getRed())
                    , countEntropy(this.imageStats.getBlueOccurrences(), this.imageStats.getBlue())
                    , countEntropy(this.imageStats.getGreenOccurrences(), this.imageStats.getGreen())
                    , countEntropy(this.imageStats.getAllSignOccurrences(), this.imageStats.getSigns())};

            this.updateBestResults(f_idx, current, entropyResults, bestFunctions);
            printColorsEntropy();
        }
        printBestResults(entropyResults, bestFunctions);
    }

    private ImageStats.Pixel getAdjacentPixels(int i, int j, ImageStats.Pixel[][] pixels) {
        return (i >= 0 && i < pixels.length && j >= 0 && j < pixels[i].length) ? pixels[i][j] : new ImageStats.Pixel(0, 0, 0);
    }

    private void updateBestResults(int f_idx, double[] current, double[] entropyResults, int[] bestFunctions) {
        for (int i = 0; i < entropyResults.length; i++) {
            if (current[i] < entropyResults[i]) {
                entropyResults[i] = current[i];
                bestFunctions[i] = f_idx;
            }
        }
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

    public void printColorsEntropy() {
        System.out.println(String.format("%-32s", "\u001b[38;5;160mRed\u001b[0m entropy") + countEntropy(this.imageStats.getRedOccurrences(), this.imageStats.getRed()));
        System.out.println(String.format("%-32s", "\u001b[38;5;26mBlue\u001b[0m entropy") + countEntropy(this.imageStats.getBlueOccurrences(), this.imageStats.getBlue()));
        System.out.println(String.format("%-32s", "\u001b[38;5;82mGreen\u001b[0m entropy") + countEntropy(this.imageStats.getGreenOccurrences(), this.imageStats.getGreen()) + "\n\n");
    }

    public void printFileEntropy() {
        System.out.println(String.format("%-18s", "File entropy") + countEntropy(this.imageStats.getAllSignOccurrences(), this.imageStats.getSigns()));
    }

    public void printBestResults(double[] bestResults, int[] functions) {
        String response = "\u001b[38;5;205m| | | | | |\n".concat("| |C|B|D| |\n").concat("| |A|X| | |\n")
                .concat("| | | | | |\u001b[0m\n\n").concat("Predictors numeration:\n").concat("0. A\n").concat("1. B\n")
                .concat("2. C\n").concat("3. A+B-C\n").concat("4. A+(B-C)/2\n").concat("5. B+(A-C)/2\n")
                .concat("6. (A+B)/2\n").concat("7. New standard\n")
                .concat(String.format("\n%-18s %d -> %f", "Best file entropy", functions[3], bestResults[3]))
                .concat(String.format("\n%-18s %d -> %f", "Best \u001b[38;5;160mred \u001b[0mentropy", functions[0], bestResults[0]))
                .concat(String.format("\n%-18s %d -> %f", "Best \u001b[38;5;26mblue \u001b[0mentropy", functions[1], bestResults[1]))
                .concat(String.format("\n%-18s %d -> %f", "Best \u001b[38;5;82mgreen \u001b[0mentropy", functions[2], bestResults[2]));
        System.out.println(response);
    }
}
