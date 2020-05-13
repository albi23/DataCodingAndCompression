import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class VectorQuantization {

    private final int k;
    private final Pixel[] pixels;
    private final Pixel initialVector = new Pixel(128, 128, 128);
    private final List<CodeVector> codeWords;
    private final Random random = new Random(new Date().getTime());
    private static final int randomRange = 40;


    public VectorQuantization(Pixel[] pixels, int k) {
        this.pixels = pixels;
        this.k = k;
        codeWords = new ArrayList<>((int) Math.pow(2, k));
    }

    public void initDictionary() {
        short numberOfDivisions = 0;
        codeWords.add(new CodeVector(initialVector));
        while (numberOfDivisions++ < this.k) {
            this.addDisorderToVectors();
            this.LBGAlgorithm();
        }
    }

    private void addDisorderToVectors() {
        for (int i = codeWords.size() - 1; i >= 0; i--) {
            CodeVector codeWord = codeWords.get(i);
            Pixel representative = codeWord.getRepresentative();
            final double blue = representative.getBlue();
            final double green = representative.getGreen();
            final double red = representative.getRed();
            representative.setBlue(mod256(blue + random.nextInt(randomRange)));
            representative.setGreen(mod256(green + random.nextInt(randomRange)));
            representative.setRed(mod256(red + random.nextInt(randomRange)));
            this.codeWords.add(new CodeVector(
                    new Pixel(
                            mod256(red - random.nextInt(randomRange)),
                            mod256(green - random.nextInt(randomRange)),
                            mod256(blue - random.nextInt(randomRange))
                    )
            ));

        }
    }

    private static double mod256(double x) {
        double value = x % 256;
        return (value < 0 ? ((int) value) & 0xFF : value);
    }

    /**
     * Linde-Buzo-Gray (LBG) Algorithm
     */
    private void LBGAlgorithm() {
        for (CodeVector c : codeWords) c.clearMember();
        this.clusterDataVectors();
        this.determinationCentroid();
        final double globalQuantizationError = this.getGlobalQuantizationError();
        System.out.println(globalQuantizationError);

    }


    private void clusterDataVectors() {
        double shortestDistance = Double.MAX_VALUE;
        int parentIdx = -1;
        for (Pixel p : pixels) {
            for (int i = 0, codeWordsSize = codeWords.size(); i < codeWordsSize; i++) {
                Pixel parent = codeWords.get(i).getRepresentative();
                final double distance = p.getDistanceToPixel(parent);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    parentIdx = i;
                }
            }
            codeWords.get(parentIdx).addMember(p);
            shortestDistance = Double.MAX_VALUE;
            parentIdx = -1;
        }
    }

    private void determinationCentroid() {
        double dx = 0, dy = 0, dz = 0;
        for (CodeVector cv : codeWords) {
            final List<Pixel> nearestMembers = cv.getNearestMembers();
            for (Pixel p : nearestMembers) {
                dx += p.getRed();
                System.out.print(p.getRed()+", ");
                dy += p.getGreen();
                dz += p.getBlue();
            }
            final Pixel representative = cv.getRepresentative();
            representative.setRed(dx / nearestMembers.size());
            representative.setGreen(dy / nearestMembers.size());
            representative.setBlue(dz / nearestMembers.size());
        }
        System.out.println("=======================");
    }

    private double getGlobalQuantizationError(){
        double err = 0D;
        for (CodeVector cv : codeWords) {
            final Pixel representative = cv.getRepresentative();
            for (Pixel p : cv.getNearestMembers()) {
                err += representative.getDistanceToPixel(p);
            }
        }
        return err;
    }


}
