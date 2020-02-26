import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Map;

public class DataCollectorTest {

    public static void main(String[] args) throws IOException {

        DataCollectorTest t = new DataCollectorTest();
//        t.process(new File("/home/albert/Desktop/3.Semestr VI/3.Kodowanie i kompresja danych/2.Laborki/testy1/test2.bin"));

        DataTestReader dataTestReader = new DataTestReader();
        final Map<Byte, DataCollector> symbolsData = dataTestReader.getSymbolsData();
        t.printData(symbolsData);
        System.out.println("Entropy : "+dataTestReader.countEntropy(symbolsData));
        System.out.println("Entropy : "+dataTestReader.countConditionalEntropy(symbolsData));
    }

    private void process(File file) throws IOException {
        try (RandomAccessFile data = new RandomAccessFile(file, "r")) {
            byte[] eight = new byte[1];
            for (long i = 0, len = data.length() ; i < len; i++) {
                data.readFully(eight);
                System.out.println(Arrays.toString(eight));
//                System.out.println("|" + new String(eight, Charset.defaultCharset()) + "|" + i);
            }
        }
    }

    private void printData(Map<Byte, DataCollector> symbolsData) {
        symbolsData.forEach((k, v) -> {
            System.out.print("\u001b[48;5;28m" + k + "\u001b[0m [" + String.format("%6d",v.getSymbolOccurrences()) + "]");
            final StringBuilder stringBuilder = new StringBuilder("--> \t {");
            v.getNeighborsOccurrences().forEach((k2, v2) -> {
                stringBuilder.append(" \u001b[48;5;20m").append(k2).append("\u001b[0m").append('[').append(v2).append("],");
            });
            stringBuilder.setLength(stringBuilder.length()-1);
            System.out.println(stringBuilder.toString().concat(" }"));
        });
    }

}
