import java.util.Map;

public class DataCollectorTest {

    public static void main(String[] args) {

        DataTestReader dataTestReader = (args.length > 0) ? new DataTestReader(args[0]) : new DataTestReader();
        final Map<Byte, DataCollector> symbolsData = dataTestReader.getSymbolsData();
        if (args.length > 1 && args[2].equals("-t")) dataTestReader.printData(symbolsData);
        System.out.println("Entropy             : " + String.format("%f", dataTestReader.countEntropy(symbolsData)));
        System.out.println("Conditional Entropy : " + String.format("%f", dataTestReader.countConditionalEntropy(symbolsData)));
    }
}
