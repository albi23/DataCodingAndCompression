package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;


public final class Utility {

    private Utility() {
    }

    public static void isCorrectFilePath(String ...paths) throws FileNotFoundException, IllegalArgumentException {
        for (String path : paths) {
            File tmpDir = new File(path);
            if (!tmpDir.exists())
                throw new FileNotFoundException(String.format("File in path %s does not exists", path));
            if (!tmpDir.isFile())
                throw new IllegalArgumentException(String.format("Path %s does not contains file", path));
        }
    }

    public static List<Long> getFilesSize(String ...filesPaths) throws FileNotFoundException {
        isCorrectFilePath(filesPaths);
        return Arrays.stream(filesPaths)
                .map(path -> new File(path).length())
                .collect(toList());
    }

    public static double countEntropy(long allSymbolsOccurrences, int[] symbolsData) {
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
