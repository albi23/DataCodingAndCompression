import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DataTestReader {

    private static final String testDirectory = System.getProperty("user.dir").concat("/LaboratoryFirst/src/main/resources/testy1");
    private static final int BLOCK_SIZE = 1;
    private final Map<Byte, DataCollector> symbolsData = new HashMap<>();
    private static long allSymbolsOccurrences;

    public DataTestReader() {
        this.collectDataFromFile(showAndSelectFileToTest());
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
            Byte lastSign = 0;
            symbolsData.put(lastSign, new DataCollector(0));
            allSymbolsOccurrences = fileChannel.size();
            while (fileChannel.position() < fileChannel.size()) {
                ByteBuffer buff = ByteBuffer.allocate(BLOCK_SIZE);
                fileChannel.read(buff);
                Byte fileContent = buff.array()[0];
                this.manageFrequencyOfSign(fileContent);
                this.manageFrequencyOfSignAfterGivenSign(lastSign, fileContent);
                lastSign = fileContent;
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
        for (DataCollector data : symbolsData.values()) {
            entropy += calculatePartialSum(data.getSymbolOccurrences());
        }
        return entropy;
    }

    public double countConditionalEntropy(Map<Byte, DataCollector> symbolsData){

        double conditionalEntropy = 0.0D;
         for (Map.Entry<Byte, DataCollector> entries : symbolsData.entrySet()){
             final DataCollector dataCollector = entries.getValue();
             final double[] partialEntropy = {0.0};
             dataCollector.getNeighborsOccurrences().forEach((k,v)-> partialEntropy[0] +=calculatePartialSum(v));
             final double signProbability = (double) dataCollector.getSymbolOccurrences() / (double) allSymbolsOccurrences;
             conditionalEntropy += signProbability* partialEntropy[0];
         }
         return conditionalEntropy;

    }

    private double calculatePartialSum(long symbolOccurrences){
        if (symbolOccurrences <= 0) return  0.0D;
        double probability = (double) symbolOccurrences / (double) allSymbolsOccurrences;
        final double measureInformation = -Math.log(probability);
        return measureInformation * probability;
    }

    int fromByteArray(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }
}
