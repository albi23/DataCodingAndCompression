import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * Classes presenting an example implementation of the Linde-Buzo-Gray algorithm
 *
 * @Author Albert Piekielny
 * see <a href="http://algorithm-wiki.org/wiki2/index.php?title=Linde%E2%80%93Buzo%E2%80%93Gray_algorithm">algorithm-wiki.org</a>
 */
public class VectorQuantization {

    private static final double EPSILON = 0.00_001;
    private final double colorsNumber;
    private final Pixel[] pixels;
    private List<CodeVector> codeWords;

    public VectorQuantization(Pixel[] pixels, int k) {
        this.pixels = pixels;
        this.colorsNumber = Math.pow(2.0, k);
        codeWords = new LinkedList<>();
    }

    /**
     * Meda responsible for creating the codebook.
     * The method doubles the number of code words in each step
     */
    public void initDictionary() {
        final Pixel bestMatchingCodeWord = findBestMatchingCodeWord(Arrays.stream(this.pixels), this.pixels.length);
        double avgDistance = this.getAvgDistanceFromMainCodeWord(bestMatchingCodeWord, this.pixels);
        codeWords.add(new CodeVector(bestMatchingCodeWord));
        while (codeWords.size() < this.colorsNumber) {
            this.addDisorderToVectors();
            System.out.print("\r" + String.format("%3.2f", (codeWords.size() / colorsNumber) * 100).concat("%"));
            avgDistance = this.LBGAlgorithm(avgDistance);
        }
        System.out.print('\r');
    }

    /**
     * The method adds a disturbance to a given set of code words, thus doubling the size of the dictionary
     */
    private void addDisorderToVectors() {
        LinkedList<CodeVector> codeWords = new LinkedList<>();
        final double negativeDisorder = EPSILON + 1.0;
        final double positiveDisorder = 1.0 - EPSILON;
        this.codeWords.stream().map(CodeVector::getRepresentative).forEach(pixel -> {
            codeWords.add(new CodeVector(pixel.getDisorderedPixel(positiveDisorder)));
            codeWords.add(new CodeVector(pixel.getDisorderedPixel(negativeDisorder)));
        });
        this.codeWords = codeWords;
    }

    /**
     * Linde-Buzo-Gray (LBG) Algorithm
     */
    private double LBGAlgorithm(double initAvgDistance) {
        double avgDistance = 0.0, err = EPSILON + 1.0;
        while (err > EPSILON) {
            codeWords.forEach(CodeVector::clearMember);
            this.clusterDataVectors();
            this.determinationCentroid();
            double beforeDistance = (avgDistance > 0.0) ? avgDistance : initAvgDistance;
            avgDistance = getGlobalQuantizationError();
            err = (beforeDistance - avgDistance) / beforeDistance;
        }
        return avgDistance;
    }

    /**
     * The method responsible for assigning the closest pixels away from the code word
     */
    private void clusterDataVectors() {

        /* final mark only for stream */
        final double[] shortestDistance = {Double.MAX_VALUE};
        final int[] parentIdxWithIterator = {-1, 0}; // workaround to iterate wth index in lambda

        Arrays.stream(this.pixels).forEach(pixel -> {
            parentIdxWithIterator[0] = -1; //parent index
            parentIdxWithIterator[1] = 0; // iterator
            shortestDistance[0] = Double.MAX_VALUE;
            this.codeWords.stream().map(CodeVector::getRepresentative).forEach(parentCodeVector -> {
                final double distance = pixel.getDistanceToPixel(parentCodeVector);
                if (distance < shortestDistance[0]) {
                    shortestDistance[0] = distance;
                    parentIdxWithIterator[0] = parentIdxWithIterator[1];
                }
                parentIdxWithIterator[1]++;
            });
            this.codeWords.get(parentIdxWithIterator[0]).addMember(pixel);
        });
    }

    /**
     * The method responsible for determining the middle pixel in a given set of code words
     */
    private void determinationCentroid() {
        this.codeWords.forEach(codeVector -> {
            final List<Pixel> nearestMembers = codeVector.getNearestMembers();
            final Pixel bestMatchingCodeWord = this.findBestMatchingCodeWord(nearestMembers.stream(), nearestMembers.size());
            codeVector.setRepresentative(bestMatchingCodeWord);
        });
    }

    /**
     * The method based on a given set of pixels calculates the average pixel for the entire set
     * @param pixels pixel set
     * @param size pixel size
     * @return middle vector - <code>Pixel</code> object
     */
    private Pixel findBestMatchingCodeWord(Stream<Pixel> pixels, int size) {
        final double[] avgColors = {0.0, 0.0, 0.0};
        pixels.forEach(p -> {
            avgColors[0] += p.getRed();
            avgColors[1] += p.getGreen();
            avgColors[2] += p.getBlue();
        });
        return new Pixel(avgColors[0] / size, avgColors[1] / size, avgColors[2] / size);
    }

    /**
     * The method based on the input pixel set creates a
     * new set of pixels based on representatives from the code dictionary
     * @return quantized pixel set
     */
    public Pixel[] createQuantizedPixelSet() {
        final Pixel[] quantizedPixels = new Pixel[this.pixels.length];
        final int[] iterator = {0};
        final TreeMap<Double, Pixel> distances = new TreeMap<>();
        Arrays.stream(this.pixels).forEach(pixel -> {
            this.codeWords.stream().map(CodeVector::getRepresentative)
                    .forEach(mainPixel -> distances.put(pixel.getDistanceToPixel(mainPixel), mainPixel));
            quantizedPixels[iterator[0]++] = distances.firstEntry().getValue();
            distances.clear();
        });
        return quantizedPixels;
    }

    private double getAvgDistanceFromMainCodeWord(final Pixel mainWord, Pixel[] pixels) {
        return Arrays.stream(pixels).parallel().map(pixel -> pixel.getDistanceToPixel(mainWord) / pixels.length)
                .reduce(0.0, Double::sum);
    }

    private double getGlobalQuantizationError() {
        double err = 0D;
        for (CodeVector cv : codeWords) {
            final Pixel representative = cv.getRepresentative();
            for (Pixel p : cv.getNearestMembers())
                err += representative.getDistanceToPixel(p) / pixels.length;
        }
        return err;
    }


}
