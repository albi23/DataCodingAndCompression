import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class DataTestReader {

    private static final String testDirectory = System.getProperty("user.dir").concat("/LaboratoryFirst/src/main/resources/testy1");
    private static final int BLOCK_SIZE = 8;

    public DataTestReader() {
        this.collectDataFromFile(showAndSelectFileToTest());
    }

    public String showAndSelectFileToTest() {
        File[] files = new File(testDirectory).listFiles();
        System.out.println("Choose number  0-" + files.length + " to specify file ");

        for (int i = 0; i < files.length; i++) {
            System.out.println(String.format("[%d] %s", i, files[i].getName()));
        }
        System.out.print("Chose : ");
        int choosedIndex = new Scanner(System.in).nextInt();

        if (choosedIndex < 0 || choosedIndex > files.length - 1)
            throw new IllegalArgumentException("Choosed file does not exists");

        return testDirectory.concat("/").concat(files[choosedIndex].getName());
    }

    private void collectDataFromFile(String path) {

        try (final FileChannel fileChannel = FileChannel.open(Paths.get(path), StandardOpenOption.READ)){
            ByteBuffer buff = ByteBuffer.allocate(BLOCK_SIZE);
            for (long i = 0, size = fileChannel.size() / BLOCK_SIZE; i < size; i++) {
                fileChannel.read(buff,fileChannel.position());
                String fileContent = new String(buff.array(), StandardCharsets.UTF_8);
                System.out.println("["+i+"] "+fileContent);
                fileChannel.position(fileChannel.position()+BLOCK_SIZE);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
