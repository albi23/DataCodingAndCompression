import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.Closeable;
import java.io.FileNotFoundException;

public interface StreamManager {

    default Closeable getFirstStreamInstance() throws FileNotFoundException {
        throw new NotImplementedException();
    }

    default Closeable getSecondStreamInstance() throws FileNotFoundException {
        throw new NotImplementedException();
    }

}
