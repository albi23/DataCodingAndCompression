import com.sun.istack.internal.NotNull;

import java.io.*;
import java.util.Objects;

/**
 * Base class for those covered by hamming encoding
 **/
public abstract class HammingCoding implements StreamManager {

    protected String filepath;
    protected String filepath2;

    public HammingCoding(@NotNull String filePath, @NotNull String filePath2) {
        this.filepath = Objects.requireNonNull(filePath);
        this.filepath2 = Objects.requireNonNull(filePath2);
    }

    public final void manageFiles() throws IOException {
        try (Closeable streamInstance =  getFirstStreamInstance();
             Closeable secondStreamInstance =  getSecondStreamInstance()) {
            manageFiles(streamInstance, secondStreamInstance);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    abstract protected void manageFiles(Closeable streamInstance, Closeable secondStreamInstance) throws IOException;

    @Override
    public Closeable getFirstStreamInstance() throws FileNotFoundException {
        return new BufferedInputStream(new FileInputStream(this.filepath));
    }

    @Override
    public Closeable getSecondStreamInstance() throws FileNotFoundException {
        return new BufferedOutputStream(new FileOutputStream(this.filepath2));
    }
}
