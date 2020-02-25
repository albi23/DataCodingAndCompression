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
    private final Map<String, DataCollector> symbolsData  = new HashMap<>();

    public DataTestReader() {
        this.collectDataFromFile(showAndSelectFileToTest());
    }

    public String showAndSelectFileToTest() {
        File[] files = new File(testDirectory).listFiles();
        System.out.println("Choose number  0-" + files.length + " to specify file ");

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
        try (final FileChannel fileChannel = FileChannel.open(Paths.get(path), StandardOpenOption.READ)){
            String lastSign = new String(new byte[]{0},StandardCharsets.UTF_8);
            symbolsData.put(lastSign,new DataCollector());

            while (fileChannel.position() < fileChannel.size()){
                ByteBuffer buff = ByteBuffer.allocate(BLOCK_SIZE);
                fileChannel.read(buff);
                String fileContent = new String(buff.array(), StandardCharsets.UTF_8);
//                System.out.println(fileContent);
                this.manageFrequencyOfSign(fileContent);
                this.manageFrequencyOfSignAfterGivenSign(lastSign,fileContent);
                lastSign = fileContent;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manageFrequencyOfSign(String newSign){
        if (symbolsData.containsKey(newSign)) {
            symbolsData.get(newSign).increaseSymbolOccurrences();
        } else {
            symbolsData.put(newSign, new DataCollector());
        }
    }

    private void manageFrequencyOfSignAfterGivenSign(String lastSign, String currentSign){
        if (!lastSign.equals(currentSign)) {
            final DataCollector dataCollector = symbolsData.get(lastSign);
            if (dataCollector.getNeighborsOccurrences().containsKey(currentSign)) {
                dataCollector.updateNeighborsOccurrences(currentSign);
            } else {
                dataCollector.addNewNeighbor(currentSign, 1L);
            }
        }
    }

    public Map<String, DataCollector> getSymbolsData() {
        return this.symbolsData;
    }
}
