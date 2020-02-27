import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Map;

public class DataCollectorTest {

    public static void main(String[] args) throws IOException {

        DataCollectorTest t = new DataCollectorTest();
        DataTestReader dataTestReader = new DataTestReader();
        final Map<Byte, DataCollector> symbolsData = dataTestReader.getSymbolsData();
        dataTestReader.printData(symbolsData);
        System.out.println("Entropy : " + String.format("%.3f", dataTestReader.countEntropy(symbolsData)));
        System.out.println("Entropy : " + String.format("%.3f",dataTestReader.countConditionalEntropy(symbolsData)));
    }
}
