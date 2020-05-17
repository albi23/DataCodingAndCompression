import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import java.util.function.BiFunction;

public class TruevisionTargaImageEncoder extends TGAImageReader {
    private Pixel[] imagePixels;
    private final int k;
    private BufferedImage decoded;

    public TruevisionTargaImageEncoder(String filepath, int k) throws IOException {
        this.k = k;
        if (k <= 0) throw new IllegalArgumentException("k = " + k);
        this.getBufferedImage(Objects.requireNonNull(filepath));
    }

    @Override
    public BufferedImage decode(byte[] buff) {
        decoded = super.decode(buff);
        return decoded;
    }

    @Override
    protected int[] createArrayWithPixels(int width, int height, byte[] buff) {
        int n = width * height;
        this.imagePixels = new Pixel[n];
        int descriptor = buff[17] & 0xFF;
        BiFunction<Integer, Integer, Integer> layoutIdx = this.getDefiningLayoutFunction(descriptor, width, height);

        if (buff[2] == 0x02 && buff[16] == 0x20) { // uncompressed RGBA
            throw new UnsupportedOperationException("Image has RGBA color definition!");
        } else if (buff[2] == 0x02 && buff[16] == 0x18) {  // uncompressed RGB
//                pixels[idx++] = (a << 24) | (r << 16) | (g << 8) | b;
            for (int i = 0, offset; i < height; i++) {
                offset = 18 + 3 * width * i;
                for (int j = 0; j < width; j++) {
                    int index = offset + 3 * j;
                    int b = buff[index] & 0xFF;
                    int g = buff[index + 1] & 0xFF;
                    int r = buff[index + 2] & 0xFF;
                    int a = 0xFF;
//                        this.imagePixels[layoutIdx.apply(i,j)] = (r << 16) | (g << 8) | (b << 0) | (a << 24);
                    this.imagePixels[layoutIdx.apply(i, j)] = new Pixel(r, g, b);
                }
            }

        } else {
            throw new UnsupportedOperationException("Image is compressed!");
        }
        return createPixelArray(n);
    }

    private int[] createPixelArray(int size) {
        final VectorQuantization vectorQuantization = new VectorQuantization(this.imagePixels, k);
        vectorQuantization.initDictionary();
        return vectorQuantization.produceQuantizedBitmap();
    }

    public BufferedImage getBufferedImage() {
        return decoded;
    }
}
