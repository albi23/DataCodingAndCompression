import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * Class responsible for processing images in TGA format
 *
 * @author Albert Piekielny
 */
class TGAImageReader {

    /**
     * 4 bytes	Extension offset
     * 4 bytes	Developer area offset
     * 16 bytes	Signature
     * 1 byte	Contains "."
     * 1 byte	Contains NULL
     */
    protected static final int TGA_IMAGE_FOOTER = 26;
    protected static final int TGA_IMAGE_HEADER = 12;
    protected static final int RIGHT_ORIGIN = 0x10;
    protected static final int UPPER_ORIGIN = 0x20;
    private static int offset = 0;

    public TGAImageReader() { }

    public BufferedImage getBufferedImage(String filepath) throws IOException {
        return decode(readFileAsBytes(filepath));
    }

    protected byte[] readFileAsBytes(String path) throws IOException {
        return Files.readAllBytes(Paths.get(path));
    }

    private static int byteToInt(byte b) {
        return b & 0xFF;
    }

    protected static int read(byte[] buf) {
        return byteToInt(buf[offset++]);
    }

    /**
     * TGA header specification:
     * <ul>
     *     <li>buff[2] image type code 0x02=uncompressed</li>
     *     <li>buff[12]+[13] image width </li>
     *     <li>buff[14]+[15] image height </li>
     *     <li>buff[16]=image pixel 0x20=32bit (RGBA), 0x18=24bit(RGB) </li>
     * </ul>
     *
     * @param buff byte array of image file
     * @return - <code>BufferedImage object</code>
     */
    public BufferedImage decode(byte[] buff) {

        for (int i = 0; i < 12; i++)
            read(buff);
        int width = read(buff) + (read(buff) << 8);
        int height = read(buff) + (read(buff) << 8);
        read(buff);
        read(buff);// skip all header data

        int[] pixels = createArrayWithPixels(width, height, buff);

        return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB) {{
            setRGB(0, 0, width, height, pixels, 0, width);
        }};
    }

    /**
     * @param width image width
     * @param height image height
     * @param buff buffered image array
     * @return pixel array constructed as RGB(A) array
     */
    protected int[] createArrayWithPixels(int width, int height, byte[] buff) {
        int n = width * height;
        int[] pixels = new int[n];
        int idx = 0;

        final boolean uncompressed = buff[2] == 0x02;
        if (uncompressed && buff[16] == 0x20) { // uncompressed RGBA
            while (n > 0) {
                pixels[idx++] = definePixelsAsRGB(buff);
                n -= 1;
            }
        } else if (uncompressed && buff[16] == 0x18) {  // uncompressed RGB
            while (n > 0) {
                pixels[idx++] = definePixelsAsARGB(buff);
                n -= 1;
            }
        } else { // compressed
            while (n > 0) {
                int byteCount = read(buff);
                if ((byteCount & 0x80) == 0) { // 0x80=dec 128, bits 10000000
                    for (int i = 0; i <= byteCount; i++) {
                        int b = read(buff), g = read(buff), r = read(buff);
                        pixels[idx++] = 0xff000000 | (r << 16) | (g << 8) | b;
                    }
                } else {
                    byteCount &= 0x7f;
                    int b = read(buff), g = read(buff), r = read(buff);
                    int v = 0xff000000 | (r << 16) | (g << 8) | b;
                    for (int i = 0; i <= byteCount; i++)
                        pixels[idx++] = v;
                }
                n -= byteCount + 1;
            }
        }
        return pixels;
    }

    protected static int definePixelsAsRGB(byte[] buff) {
        int b = read(buff);
        int g = read(buff);
        int r = read(buff);
        int a = read(buff);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    protected static int definePixelsAsARGB(byte[] buff) {
        int b = read(buff);
        int g = read(buff);
        int r = read(buff);
        int a = 255; // full color
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}