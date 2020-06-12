import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Random;

public class BytesNoise extends HammingCoding {

    private final double probability;
    private static Random rand;

    public BytesNoise(String inFilePath, String outFilePath, double probability) {
        super(inFilePath, outFilePath);
        this.probability = probability;
        rand = new Random(new Date().getTime());
    }


    @Override
    protected void manageFiles(Closeable is, Closeable os) throws IOException {
        for (int bit = ((InputStream) is).read(); bit != -1; bit = ((InputStream) is).read()) {
            ((OutputStream)os).write(BitsUtils.toByteValue(addDisorder(BitsUtils.bitValueRepresentation(bit))));
        }
    }

    private int[] addDisorder(int[] valueRepresentation) {
        for (int i = 0, length = valueRepresentation.length; i < length; i++) {
            if (rand.nextDouble() > this.probability) {
                valueRepresentation[i] = valueRepresentation[i] ^ 1;
            }
        }
        return valueRepresentation;
    }
}
