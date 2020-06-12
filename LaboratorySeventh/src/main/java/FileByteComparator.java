import java.io.*;

public class FileByteComparator extends HammingCoding {

    private int differences;

    public FileByteComparator(String filePath, String filePath2) {
        super(filePath, filePath2);
    }

    @Override
    protected void manageFiles(Closeable is, Closeable is2) throws IOException {
        for (int bit = ((InputStream) is).read(), bit2 = ((InputStream) is2).read();
             bit != -1 && bit2 != -1; bit = ((InputStream) is).read(), bit2 = ((InputStream) is2).read()) {
            if (isLeadingBitsDifference(bit, bit2)) differences++;
            if (isTrailingBitsDifference(bit, bit2)) differences++;
        }
        System.out.println(String.format("Detect %d 4th length bytes differences", differences));
    }

    private boolean isLeadingBitsDifference(int bitValue1, int bitValue2) {
        return bitValue1 >> 4 == bitValue2 >> 4;
    }

    private boolean isTrailingBitsDifference(int bitValue1, int bitValue2) {
        return bitValue1 << 28 == bitValue2 << 28;
    }

    @Override
    public Closeable getSecondStreamInstance() throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(filepath2));
    }
}
