import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

public class HammingEncode extends HammingCoding {


    public HammingEncode(String inFilePath, String outFilePath) {
        super(inFilePath, outFilePath);
    }

    @Override
    protected void manageFiles(Closeable is, Closeable os) throws IOException {
        for (int bit = ((InputStream)is).read(); bit != -1; bit = ((InputStream)is).read()) {
            ((OutputStream)os).write(encode(BitsUtils.bitValueRepresentation(bit)));
        }
    }

    private byte[] encode(int[] data) {
        return new byte[]{
                BitsUtils.toByteValue(encode(data, 0)),
                BitsUtils.toByteValue(encode(data, 4))
        };
    }

    private int[] encode(int[] array, int offset) {
        final int[] tmp = new int[8];
        System.arraycopy(array, offset, tmp, 0, 4);
        tmp[4] = (array[1 + offset] ^ array[2 + offset] ^ array[3 + offset]);
        tmp[5] = (array[offset] ^ array[2 + offset] ^ array[3 + offset]);
        tmp[6] = (array[offset] ^ array[1 + offset] ^ array[3 + offset]);
        tmp[7] = Arrays.stream(tmp).reduce(0, Integer::sum) % 2;
        return tmp;
    }

}
