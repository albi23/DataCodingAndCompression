import org.junit.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BaseTest {

    @Test
    public void runAll() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> MainQuantization.main("","","25"));
        assertTrue(exception.getMessage().contains("Incorrect File Extension"));
    }

    @Test
    public void whenDerivedExceptionThrown_thenAssertionSucceds() {
        Exception exception = assertThrows(RuntimeException.class, () -> Integer.parseInt("1a"));
        String expectedMessage = "For input string";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
