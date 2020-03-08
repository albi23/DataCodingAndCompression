import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class DataTestReader {

    private static final String testDirectory = System.getProperty("user.dir").concat("/LaboratoryFirst/src/main/resources/testy1");
    private final Map<Byte, DataCollector> symbolsData = new ConcurrentHashMap<>();
    private static double allSymbolsOccurrences;

    public DataTestReader() {
        this.collectDataFromFile(showAndSelectFileToTest());
    }

    public DataTestReader(String pathToFile) {
        this.collectDataFromFile(pathToFile);
    }

    public String showAndSelectFileToTest() {
        File[] files = new File(testDirectory).listFiles();
        System.out.println("Choose number  0 |-> " + files.length + " to specify file ");

        for (int i = 0; i < files.length; i++) {
            System.out.println(String.format("[%d] %s", i, files[i].getName()));
        }
        System.out.print("Chose : ");
        int choosedIndex = new Scanner(System.in).nextInt();

        if (choosedIndex < 0 || choosedIndex > files.length - 1)
            throw new IllegalArgumentException("Choosed file does not exists");

        return testDirectory.concat("/").concat(files[choosedIndex].getName());
    }

    private void collectDataFromFile(String path) {
        try (final FileChannel fileChannel = FileChannel.open(Paths.get(path), StandardOpenOption.READ)) {
            byte previousByte = 0;
            symbolsData.put(previousByte, new DataCollector(0));
            allSymbolsOccurrences = fileChannel.size();
            ByteBuffer buffer = ByteBuffer.allocate((int) allSymbolsOccurrences);
            fileChannel.read(buffer);
            buffer.flip();
            for (byte currentByte : buffer.array()) {
                this.manageFrequencyOfSign(currentByte);
                this.manageFrequencyOfSignAfterGivenSign(previousByte, currentByte);
                previousByte = currentByte;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manageFrequencyOfSign(Byte newSign) {
        if (symbolsData.containsKey(newSign)) {
            symbolsData.get(newSign).increaseSymbolOccurrences();
        } else {
            symbolsData.put(newSign, new DataCollector());
        }
    }

    private void manageFrequencyOfSignAfterGivenSign(Byte lastSign, Byte currentSign) {
            final DataCollector dataCollector = symbolsData.get(lastSign);
            if (dataCollector.getNeighborsOccurrences().containsKey(currentSign)) {
                dataCollector.updateNeighborsOccurrences(currentSign);
            } else {
                dataCollector.addNewNeighbor(currentSign, 1L);
            }
    }

    public Map<Byte, DataCollector> getSymbolsData() {
        return this.symbolsData;
    }

    public double countEntropy(Map<Byte, DataCollector> symbolsData) {
        double entropy = 0.0D;
        double logFromAll = log2(allSymbolsOccurrences);
        for (DataCollector data : symbolsData.values()) {
            final long symbolOccurrences = data.getSymbolOccurrences();
            if (symbolOccurrences <= 0) continue;
            entropy += symbolOccurrences * (logFromAll - log2(symbolOccurrences));
        }
        return entropy/allSymbolsOccurrences;
    }

    public double countConditionalEntropy(Map<Byte, DataCollector> symbolsData) {

        double conditionalEntropy = 0.0;
        for (Map.Entry<Byte, DataCollector> parentSign : symbolsData.entrySet()) {
            final DataCollector dataCollector = parentSign.getValue();
            for (Long occurrences : dataCollector.getNeighborsOccurrences().values()) {
                if (occurrences == 0.0 || dataCollector.getSymbolOccurrences() == 0.0) continue;
                conditionalEntropy += (occurrences * (log2(dataCollector.getSymbolOccurrences()) - log2(occurrences)));
            }
        }
        return conditionalEntropy/(allSymbolsOccurrences);
    }

    double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    public void printData(Map<Byte, DataCollector> symbolsData) {
        symbolsData.forEach((k, v) -> {
            System.out.print("\u001b[48;5;28m" + String.format("%3d",k) + "\u001b[0m [" + String.format("%6d",v.getSymbolOccurrences()) + "]");
            final StringBuilder stringBuilder = new StringBuilder("--> \t {");
            v.getNeighborsOccurrences().forEach((k2, v2) -> {
                stringBuilder.append(" \u001b[48;5;20m").append(k2).append("\u001b[0m").append('[').append(v2).append("],");
            });
            stringBuilder.setLength(stringBuilder.length()-1);
            System.out.println(stringBuilder.toString().concat(" }"));
        });
    }
}
