import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;


public class Main {

    private short methodState = 0;
    private EliasCodes eliasCodes;

    public static void main(String[] args) throws IOException {
        if (args.length < 4 || args.length > 5) {
            System.err.println("Usage: java  Main --encode --name_of_alg -file inputFilePath outFilePath");
            System.err.println("Usage: java  Main --decode --name_of_alg -file inputFilePath outFilePath");
            System.err.println("name_of_alg : gamma, delta fibb omega");
            System.exit(1);
        }
        Main main = new Main();
        switch (args[1]) {
            case "--delta":
                main.methodState = 1;
                break;
            case "--fibb":
                main.methodState = 2;
                break;
            case "--gamma":
                main.methodState = 3;
                break;
            default:
                main.methodState = 0;
        }
        int inputIndex = (args.length == 4) ? 2 : 3;
        main.eliasCodes = new ElisaOmega().getInstance(main.methodState);
        if (args[0].equals("--encode")) {
            main.encodeFile(args[inputIndex],args[inputIndex+1]);
        } else if (args[0].equals("--decode")) {
            main.decodeFile(args[inputIndex],args[inputIndex+1]);
        } else {
            throw new UnsupportedOperationException(String.format("No support for %s option", args[0]));
        }
    }

    private void decodeFile(String inputFilePath, String outFilePath) throws IOException {
        final String code = readAllWordsFromBigFiles(inputFilePath);
        final Stack<Integer> codes = this.eliasCodes.decode(code);
        Map<Integer, String> mapping = new HashMap<>(256);

        for (int i = 0; i < 256; i++) {
            mapping.put(i + 1, new String(new char[]{(char) (i)}));
        }
        saveToFile(outFilePath, decodeLZW(codes, mapping));
    }

    private void encodeFile(String inputFilePath,String outFilePath) throws IOException {
        Map<String, Integer> mapping = new HashMap<>(256);

        for (int i = 0; i < 256; i++) {
            mapping.put(new String(new char[]{(char) (i)}), i + 1);
        }

        final byte[] bytes = readFile(inputFilePath);
        final long fileLength = bytes.length * 8;
        final List<Integer> signOccurrences = signOccurrences(bytes);
        final ArrayList<Integer> lzwEncoded = this.encodeLZW(bytes, mapping);
        final String code = lzwEncoded.stream().map(this.eliasCodes::encode).collect(Collectors.joining());
        printEncodedInfo(signOccurrences, fileLength, code, fileLength);
        saveBytesToFile(outFilePath, code);

    }

    private static List<Integer> signOccurrences(byte[] bytes) {
        int[] occurences = new int[257];
        for (byte b : bytes) {
            occurences[b & 0xFF]++;
        }
        return Arrays.stream(occurences).filter(x -> x > 0).boxed().collect(Collectors.toList());
    }

    private byte[] readFile(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    private void saveToFile(String outputPath, String encoded) throws IOException {
        Files.write(Paths.get(outputPath), encoded.getBytes());
    }

    private void saveBytesToFile(String outputPath, String encoded) throws IOException {
        final byte[] bytes = new byte[(int) Math.ceil(encoded.length() / 8.0)];
        int index = -1, slice = 8;
        encoded = (this.methodState == 0) ? encoded.concat(StringUtils.leftPad("", 8 - (encoded.length() % 8), '1')) :
                encoded.concat(StringUtils.rightPad("", 8 - (encoded.length() % 8), '0'));

        while (++index < bytes.length) {
            bytes[index] = (byte) Integer.parseInt(encoded.substring(0, slice), 2);
            encoded = encoded.substring(slice);
            if (encoded.length() < 8) slice = encoded.length();
        }

        Files.write(Paths.get(outputPath), bytes);
    }
    public static String readAllWordsFromBigFiles(String pathToResource) throws IOException {
        FileChannel chanel = FileChannel.open(Paths.get(pathToResource), StandardOpenOption.READ);
        ByteBuffer buffer = ByteBuffer.allocate((int) chanel.size());
        chanel.read(buffer);
        buffer.flip();
        StringBuilder sb = new StringBuilder();
        for (byte b : buffer.array()) {
            final String str = Integer.toBinaryString(b & 0xFF);
            sb.append(StringUtils.leftPad(str, 8, '0'));
        }
        chanel.close();

        return sb.toString();
    }

    private ArrayList<Integer> encodeLZW(byte[] bufferedText, Map<String, Integer> mapping) {
        int counter = mapping.size() + 1;
        String sign = new String(new char[]{(char) (bufferedText[0] & 0xFF)});
        final ArrayList<Integer> out = new ArrayList<>();

        for (int i = 1; i < bufferedText.length; i++) {
            final String bufSign = new String(new char[]{(char) (bufferedText[i] & 0xFF)});
            String contacted = sign.concat(bufSign);
            if (mapping.get(sign.concat(bufSign)) != null) {
                sign = sign.concat(bufSign);
            } else {
                out.add(mapping.get(sign));
                mapping.put(contacted, counter);
                counter++;
                sign = bufSign;
            }
        }
        out.add(mapping.get(sign));
        return out;
    }

    private String decodeLZW(Stack<Integer> codes, Map<Integer, String> mapping) {
        int counter = mapping.size() + 1;
        Integer pk = codes.get(0);
        StringBuilder out = new StringBuilder(mapping.get(pk));
        for (Integer code : codes.subList(1, codes.size())) {
            final String str = mapping.get(pk);
            if (mapping.get(code) != null) {
                String tmp = mapping.get(code);
                mapping.put(counter, str.concat(tmp.substring(1)));
                out.append(tmp);
            } else {
                String binary = str.concat(str.substring(0,1));
                mapping.put(counter, binary);
                out.append(binary);
            }
            pk = code;
            counter++;
        }
        return out.toString();
    }

    private void printEncodedInfo(List<Integer> signTextOccurrences, long allOccurrences, String code, long originalFileSize) {
        final double textEntropy = countEntropy(allOccurrences / 8, signTextOccurrences.toArray(new Integer[signTextOccurrences.size()]));

        int codeLength = code.length();
        Integer[] occurrences = new Integer[257];
        Arrays.fill(occurrences, 0);
        while (code.length() > 8) {
            occurrences[Integer.parseInt(code.substring(0, 8), 2)]++;
            code = code.substring(8);
        }
        if (code.length() > 0)
            occurrences[Integer.parseInt(code, 2)]++;


        System.out.println(String.format("%-20s","Input file size ") + originalFileSize/8);
        System.out.println(String.format("%-20s","Compress file size ") + Math.ceil(codeLength/8.0));
        System.out.println(String.format("%-20s","Compression rate ")+ (originalFileSize) / (double) codeLength);
        System.out.println(String.format("%-20s","Text entropy ")+ textEntropy);
        System.out.println(String.format("%-20s","Code entropy ") + countEntropy(codeLength / 8, occurrences));
    }


    public static double countEntropy(long allSymbolsOccurrences, Integer[] symbolsData) {
        double entropy = 0.0D;
        double logFromAll = log2(allSymbolsOccurrences);
        for (int data : symbolsData) {
            if (data <= 0) continue;
            entropy += data * (logFromAll - log2(data));
        }
        return entropy / allSymbolsOccurrences;
    }

    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}
