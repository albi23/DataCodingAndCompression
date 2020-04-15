import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;


public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: java  Main --encode --alg inputFile outputFile ");
            System.err.println("Usage: java  Main --decode --alg inputFile outputFile");
            System.exit(1);
        }

        Main main = new Main();
        main.encodeFile("LaboratoryThird/src/main/java/test.txt");
    }

    private void decodeFile(String path) throws IOException {
        final String code = readAllWordsFromBigFiles(path);

    }

    private void   encodeFile(String path) throws IOException {
        Map<String, Integer> mapping = new HashMap<>(256);

        for (int i = 0; i < 256; i++) {
            mapping.put(new String(new char[]{(char) (i)}), i+1);
        }

        final byte[] bytes = readFile(path);
        final long fileLength = bytes.length*8;
        final List<Integer> signOccurrences = signOccurrences(bytes);
        final ArrayList<Integer> lzwEncoded = this.encodeLZW(bytes, mapping);
        final String code = lzwEncoded.stream().map(this::eliasGamma).collect(Collectors.joining());
        printEncodedInfo(signOccurrences,fileLength,code,fileLength);
        saveToFile("encodedfile",code);

    }

    private static List<Integer> signOccurrences(byte[] bytes){
        int[] occurences = new int[257];
        for (byte b: bytes) {
            occurences[b & 0xFF]++;
        }
        return Arrays.stream(occurences).filter(x -> x > 0).boxed().collect(Collectors.toList());
    }

    private  byte[] readFile(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    private void saveToFile(String outputPath,String encoded) throws IOException {
        Files.write(Paths.get(outputPath),encoded.getBytes());
    }

    public static String readAllWordsFromBigFiles(String pathToResource) throws IOException {
        CharBuffer charBuffer = null;
        FileChannel chanel = FileChannel.open(Paths.get(pathToResource), StandardOpenOption.READ);
        MappedByteBuffer buffer = chanel.map(FileChannel.MapMode.READ_ONLY, 0, chanel.size());
        if (buffer != null) charBuffer = StandardCharsets.UTF_8.decode(buffer);
        chanel.close();

        return (charBuffer != null) ? charBuffer.toString() : "";
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

    private String eliasGamma(int num){
        String response = "";
        if (num > 0 ){
            int tmp = (int) (Math.log(num) / Math.log(2));
            final String binaryString = Integer.toBinaryString(num);
            response = StringUtils.leftPad(response,tmp,'0').concat(binaryString);
        }
        return response;
    }

//    private void eliasGammaDecode(String code){
//
//        final List<Integer> response = new ArrayList<>();
//        while (code.length()>0){
//            code.
//        }
//    }

    private void printEncodedInfo(List<Integer> signTextOccurrences,long allOccurrences,String code, long originalFileSize){
        final double textEntropy = countEntropy(allOccurrences / 8, signTextOccurrences.toArray(new Integer[signTextOccurrences.size()]));
        int zero = 0;
        for (char c : code.toCharArray()){
            if(c == 48) zero++;
        }
        final Integer[] ints = {zero, code.length() - zero};

        System.out.println("Input file size : "+originalFileSize);
        System.out.println("Compress file size : "+code.length());
        System.out.println("Compression rate : "+(originalFileSize)/(double)code.length());
        System.out.println("Text entropy : "+textEntropy);
        System.out.println("Code entropy : "+countEntropy(code.length(),ints));
    }


    public static double countEntropy(long allSymbolsOccurrences, Integer[] symbolsData) {
        double entropy = 0.0D;
        double logFromAll = log2(allSymbolsOccurrences);
        for (int data : symbolsData) {
            if (data <= 0) continue;
            entropy += data * (logFromAll - log2(data));
        }
        return entropy/allSymbolsOccurrences;
    }

    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }
}
