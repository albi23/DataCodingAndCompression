import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Stream;

public class VectorQuantization {

    private List<CodeVector> codeWords;
    private final int k;
    private final Pixel[] pixels;
    private static final double EPSILON = 0.0_0001;


    public VectorQuantization(Pixel[] pixels, int k) {
        this.pixels = pixels;
        this.k = k;
        codeWords = new LinkedList<>();
    }

    public void initDictionary() {
        short numberOfDivisions = 0;
        final Pixel bestMatchingCodeWord = findBestMatchingCodeWord(Arrays.stream(this.pixels), this.pixels.length);
        double avgDistance = this.getAvgDistanceFromMainCodeWord(bestMatchingCodeWord, this.pixels);
        codeWords.add(new CodeVector(bestMatchingCodeWord));
        while (numberOfDivisions++ < this.k) {
            this.addDisorderToVectors();
            System.out.println("Spliting : "+codeWords.size());
            avgDistance = this.LBGAlgorithm(avgDistance);
        }
        System.out.println(codeWords);
    }

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

    private static double mod256(double x) {
        double value = x % 256;
        return (value < 0 ? ((int) value) & 0xFF : value);
    }

    /**
     * Linde-Buzo-Gray (LBG) Algorithm
     */
    private double LBGAlgorithm(double initAvgDistance) {
        double avgDistance = 0.0, err = EPSILON + 1.0, iteration = 0;
        for (; err > EPSILON; iteration++) {
            codeWords.forEach(CodeVector::clearMember);
            this.clusterDataVectors();
            this.determinationCentroid();
            double beforeDistance = (avgDistance > 0.0) ? avgDistance : initAvgDistance;
            System.out.println(beforeDistance);
            avgDistance = getGlobalQuantizationError();
            System.out.println(avgDistance);
            err = (beforeDistance - avgDistance)/beforeDistance;
            System.out.println(String.format("[%1.0f] err[%f]", iteration, err)+"\n");

        }
        return avgDistance;
    }


    private void clusterDataVectors() {

        /* final mark only for parallel stream */
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

    private void determinationCentroid() {
        this.codeWords.forEach(codeVector -> {
            final List<Pixel> nearestMembers = codeVector.getNearestMembers();
            final Pixel bestMatchingCodeWord = this.findBestMatchingCodeWord(nearestMembers.stream(), nearestMembers.size());
            codeVector.setRepresentative(bestMatchingCodeWord);
        });
    }

    private Pixel findBestMatchingCodeWord(Stream<Pixel> pixels, int size) {
        final double[] avgColors = {0.0, 0.0 ,0.0};
        pixels.forEach(p -> {
            avgColors[0] += p.getRed();
            avgColors[1] += p.getGreen();
            avgColors[2] += p.getBlue();
        });
        return new Pixel(avgColors[0] / size, avgColors[1] / size, avgColors[2] / size);
    }

    public int[] produceQuantizedBitmap(){
        final int[] quantizedPixels = new int[this.pixels.length];
        final int[] iterator = {0};
        final TreeMap<Double, Pixel> distances = new TreeMap<>();
        Arrays.stream(this.pixels).forEach(pixel -> {
            this.codeWords.stream().map(CodeVector::getRepresentative).forEach(mainPixel-> distances.put(pixel.getDistanceToPixel(mainPixel), mainPixel));
            final Pixel p = distances.firstEntry().getValue();
            quantizedPixels[iterator[0]++] = (((int)p.getRed()) << 16) | (((int)p.getGreen()) << 8) | (((int)p.getBlue()) << 0) | ( 0XFF << 24);;
            distances.clear();
        });
        return quantizedPixels;
    }


    private double getAvgDistanceFromMainCodeWord(final Pixel mainWord, Pixel[] pixels) {
        return Arrays.stream(pixels).parallel().map(pixel -> pixel.getDistanceToPixel(mainWord)/ pixels.length).reduce(0.0, Double::sum);
    }


    private double getGlobalQuantizationError(){
        double err = 0D;
        for (CodeVector cv : codeWords) {
            final Pixel representative = cv.getRepresentative();
            for (Pixel p : cv.getNearestMembers()) {
                err += representative.getDistanceToPixel(p)/ pixels.length;
            }
        }
        return err;
    }


}
