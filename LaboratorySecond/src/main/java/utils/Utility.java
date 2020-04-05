package utils;

import java.io.File;
import java.io.FileNotFoundException;

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
}
