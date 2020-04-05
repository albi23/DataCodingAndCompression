import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class DataHolder {

    private HashMap<String, Integer> signOnOccurrences = new HashMap<>();
    /**
     * Constant defining the basic size of the data buffer. Default 100Mb
     */
    private static final long BASIC_BUFF_SIZE = 104_857_600L;
    private long buffSize = BASIC_BUFF_SIZE;


    public void loadDataFromFile(String path) throws FileNotFoundException, IllegalArgumentException {
        File tmpDir = new File(path);
        if (!tmpDir.exists())
            throw new FileNotFoundException(String.format("File in path %s does not exists", path));
        if (!tmpDir.isFile())
            throw new IllegalArgumentException(String.format("Path %s does not contains file", path));

        this.collectDataFromFile(path);
    }

    private void collectDataFromFile(String path) {
        final long start = System.currentTimeMillis();
        try (final FileChannel fileChannel = FileChannel.open(Paths.get(path), StandardOpenOption.READ)) {
            while (fileChannel.position() < fileChannel.size()) {
                long toAllocateMemory = Math.min(fileChannel.size() - fileChannel.position(), buffSize);
                ByteBuffer buffer = ByteBuffer.allocate((int) toAllocateMemory);
                fileChannel.read(buffer);
                buffer.flip();
                final byte[] bytes = buffer.array();
                for (byte currentByte : bytes) {
//                    int a = (currentByte << );
                    System.out.println("wczytany : " +currentByte+"   ->  "+(currentByte & 0xFF));
//                    this.signOnOccurrences.computeIfPresent(new String(new char[]{(char)currentByte}), (k, v) -> v + 1);
//                    this.signOnOccurrences.putIfAbsent(new String(new char[]{(char)(currentByte+128)}), 1);
                }
//                final byte[] array = buffer.array();
//                System.out.println(new String(Arrays.copyOfRange(array,array.length-30,array.length-1)));
//                this.createCodeWords(bytes.length);
//                this.signOnOccurrences.forEach((k, v) -> {
//                    System.out.println("[" + k + "]  = " + v);
//                });
                this.signOnOccurrences = new HashMap<>();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("[Time] : %sms", (System.currentTimeMillis() - start)));
    }

    private void createCodeWords(int numberOfLoadedCharacter) {
        final TreeMap<String, Integer> codeWorks = (TreeMap<String, Integer>) sortByValuesReserved(this.signOnOccurrences);
        final double numberOfCodeWords = Math.pow(2, Math.ceil(log2(numberOfLoadedCharacter))) - 1;
        final int treeSize = codeWorks.size();

//        while (treeSize < numberOfCodeWords){
//
//        }
        System.out.println("numberOfCodeWords " + numberOfCodeWords + " n = " + numberOfLoadedCharacter);

//        Comparator<Byte> byteComparator = Comparator.comparing(Byte::byteValue);
//        final TreeMap<Byte,Integer> codeWorks = new TreeMap<>(Comparator.comparing(Byte::byteValue));
//        codeWorks.putAll(this.signOnOccurrences);

        System.out.println("Key set : -> \n" + codeWorks.keySet().toString());
        System.out.println("Values  : -> \n" + codeWorks.values().toString());
        System.out.println(codeWorks);


//        codeWorks.putAll();
    }

    public long getBuffSize() {
        return buffSize;
    }

    public void setBuffSize(long buffSize) {
        this.buffSize = buffSize;
    }

    public HashMap<String, Integer> getSignOnOccurrences() {
        return signOnOccurrences;
    }

    public static <K, V extends Comparable<V>> Map<K, V> sortByValuesReserved(final Map<K, V> map) {
        return sortByValues(map, true);
    }

    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map, boolean reversed) {
        int direction = (reversed) ? -1 : 1;
        Comparator<K> valueComparator = (k1, k2) -> {
            int compare = direction * map.get(k1).compareTo(map.get(k2));
            return (compare == 0) ? 1 : compare; // not return  0 protect before override values
        };
        return new TreeMap(valueComparator) {{
            putAll(map);
        }};
    }

    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

}
