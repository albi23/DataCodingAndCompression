public class ImageStats {
    private int imageWidth;
    private int blueOccurrences;
    private int redOccurrences;
    private int greenOccurrences;
    private int allSignOccurrences;
    private int[] red;
    private int[] green;
    private int[] blue;
    private int[] signs;
    private Pixel[][] pixels;
    private static int idx = 0;
    private static int idy = 0;

    public ImageStats() {
        this.initRGBArrays();
        this.signs = new int[256];
    }

    public void initRGBArrays() {
        this.red = new int[256];
        this.green = new int[256];
        this.blue = new int[256];
    }

    public void initPixelsArray(int width, int height) {
        pixels = new Pixel[width][height];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                pixels[i][j] = new Pixel(0, 0, 0);
    }

    public void storePixel(int r, int g, int b) {
        int x = idx % imageWidth;
        this.pixels[x][idy] = new Pixel(r, g, b);
        if (idx == imageWidth - 1) idy++;
        idx = ++x;
    }

    public Pixel[][] getPixels() {
        return pixels;
    }

    public int[] getRed() {
        return red;
    }


    public int[] getGreen() {
        return green;
    }

    public int[] getBlue() {
        return blue;
    }

    public void increaseRed(int idx) {
        this.redOccurrences++;
        this.red[idx]++;
    }

    public void increaseBlue(int idx) {
        this.blueOccurrences++;
        this.blue[idx]++;
    }

    public void increaseGreen(int idx) {
        this.greenOccurrences++;
        this.green[idx]++;
    }

    public void increaseSigns(int... idx) {
        for (int i : idx) this.signs[i]++;
        allSignOccurrences += idx.length;
    }

    public int[] getSigns() {
        return signs;
    }

    public int getAllSignOccurrences() {
        return allSignOccurrences;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getBlueOccurrences() {
        return blueOccurrences;
    }

    public int getRedOccurrences() {
        return redOccurrences;
    }

    public int getGreenOccurrences() {
        return greenOccurrences;
    }

    static class Pixel {
        public int r, g, b;

        Pixel(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
    }
}
