import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @Author Albert Piekielny
 *
 * Class responsible for decoding the <code>filePath</code> file to the <code>filePath2</code> output file
 */
public class HammingDecoding extends HammingCoding {

    private int errors;

    public HammingDecoding(String filePath, String filePath2) {
        super(filePath, filePath2);
    }


    @Override
    protected void manageFiles(Closeable is, Closeable os) throws IOException {
        for (int bit = ((InputStream) is).read(), bit2 = ((InputStream) is).read();
             bit != -1 && bit2 != -1; bit = ((InputStream) is).read(), bit2 = ((InputStream) is).read()) {
            ((OutputStream) os).write(decode(bit, bit2));
        }
        System.out.println(String.format("Detect %d errors", errors));
    }

    private int decode(int byteVal1, int byteVal2) {
        int[] decoded = new int[8];
        final int[] encodePart1 = decode(BitsUtils.bitValueRepresentation(byteVal1));
        final int[] encodePart2 = decode(BitsUtils.bitValueRepresentation(byteVal2));
        System.arraycopy(encodePart1, 0, decoded, 0, 4);
        System.arraycopy(encodePart2, 0, decoded, 4, 4);
        return BitsUtils.toByteValue(decoded) & 0xFF;
    }

    private int[] decode(int[] array) {
        int[] decoded = new int[4];
        System.arraycopy(array, 0, decoded, 0, 4);

        int distortedBit = (array[3] ^ array[4] ^ array[5] ^ array[6]) << 2
                + (array[1] ^ array[2] ^ array[5] ^ array[6]) << 1
                + (array[0] ^ array[2] ^ array[4] ^ array[6]);

        int checksum = Arrays.stream(array).reduce(0, Integer::sum) % 2;
        if (distortedBit > 0) {
            if (checksum == 0) {
                this.errors++;
            } else {
                if (distortedBit <= 4)
                    decoded[distortedBit - 1] = decoded[distortedBit - 1] ^ 1;
            }
        }
        return decoded;
    }
}
