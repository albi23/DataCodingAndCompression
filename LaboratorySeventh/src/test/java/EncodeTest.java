import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EncodeTest {

    private static final String[] flags;
    private static final String dir;

    static {
        flags = new String[]{"code", "decode", "check", "noise"};
        dir = System.getProperty("user.dir") + File.separator + "/src/main/resources";
    }

    @Test
    @BeforeAll
    static void createTestFile() throws IOException {

        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 65; i < 256; i++) {
            for (int j = 0; j < 40; j++)
                stringBuilder.append((char) i);
            stringBuilder.append('\n');
        }
        Files.write(Paths.get(dir + "/test.txt"), stringBuilder.toString().getBytes());
    }

    @Test
    @Order(1)
    public void runEncode() throws Exception {
        final Path in = Paths.get(dir + "/test.txt");
        final Path out = Paths.get(dir + "/out.txt");
        MainCoding.main(flags[0], in.toString(), out.toString());
    }

    @Test
    @Order(2)
    public void runNoise() throws Exception {
        final Path in = Paths.get(dir + "/out.txt");
        final Path out = Paths.get(dir + "/out_noise.txt");
        final String probability = "0.05";
        MainCoding.main(flags[3], in.toString(), out.toString(), probability);
    }

    @Test
    @Order(5)
    public void runCompare() throws Exception {
        final Path in = Paths.get(dir + "/test.txt");
        final Path in2 = Paths.get(dir + "/out_decoded.txt");
        MainCoding.main(flags[2], in.toString(), in2.toString());
    }

    @Test
    @Order(4)
    public void runDecode() throws Exception {
        final Path in = Paths.get(dir + "/out_noise.txt");
        final Path out = Paths.get(dir + "/out_decoded.txt");
        MainCoding.main(flags[1], in.toString(), out.toString());
    }

    @Test
    @AfterAll
    static void cleanAfterTest() {
        Arrays.stream(Objects.requireNonNull(new File(dir).listFiles())).forEach(File::delete);
    }
}
