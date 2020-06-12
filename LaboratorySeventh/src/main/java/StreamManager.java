import java.io.Closeable;
import java.io.FileNotFoundException;

public interface StreamManager {

    default Closeable getFirstStreamInstance() throws FileNotFoundException {
        return null;
    }

    default Closeable getSecondStreamInstance() throws FileNotFoundException {
        return null;
    }

}
