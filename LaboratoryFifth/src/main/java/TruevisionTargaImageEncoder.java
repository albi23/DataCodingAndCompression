import java.io.IOException;
import java.util.Objects;

public class TruevisionTargaImageEncoder extends TGAImageReader {
    private Pixel[] imagePixels;
    private final int k;

    public TruevisionTargaImageEncoder(String filepath, int k) throws IOException {
        this.k = k;
        if (k <= 0 ) throw  new IllegalArgumentException("k = "+k);
        this.getBufferedImage(Objects.requireNonNull(filepath));
    }

    @Override
    protected int[] createArrayWithPixels(int width, int height, byte[] buff) {
        int n = width * height;
        int idx = 0;
        this.imagePixels = new Pixel[n];

        if (buff[2] == 0x02 && buff[16] == 0x20) { // uncompressed RGBA
            throw new UnsupportedOperationException("Image has RGBA color definition!");
        } else if (buff[2] == 0x02 && buff[16] == 0x18) {  // uncompressed RGB
            while (n --> 0) {
                int b = read(buff), g = read(buff), r = read(buff), a = 255;
                this.imagePixels[idx++] = new Pixel(r, g, b);
//                pixels[idx++] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        } else {
            throw new UnsupportedOperationException("Image is compressed!");
        }
        return createPixelArray(n);
    }

    private int[] createPixelArray(int size) {
        final VectorQuantization vectorQuantization = new VectorQuantization(this.imagePixels, k);
        vectorQuantization.initDictionary();
        return new int[size];
    }

}
