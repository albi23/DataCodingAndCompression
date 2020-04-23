public class ImageStats {
    private int imageWidth;
    private int allSignOccurrences;
    private int[] rgbOccurrences;
    private int[][] rgbColors;
    private int[] signs;
    private Pixel[][] pixels;
    private static int idx = 0;
    private static int idy = 0;

    public ImageStats() {
        this.initRGBArrays();
        this.signs = new int[256];
    }

    public void initRGBArrays() {
        this.rgbColors = new int[3][256];
        this.rgbOccurrences = new int[3];
    }

    public void initSignArray(){
        this.signs = new int[256];
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
        return this.rgbColors[0];
    }

    public int[] getGreen() {
        return this.rgbColors[1];
    }

    public int[] getBlue() {
        return this.rgbColors[2];
    }

    public void increaseRed(int idx) {
        this.rgbOccurrences[0]++;
        this.rgbColors[0][idx]++;
    }

    public void increaseBlue(int idx) {
        this.rgbOccurrences[2]++;
        this.rgbColors[2][idx]++;
    }

    public void increaseGreen(int idx) {
        this.rgbOccurrences[1]++;
        this.rgbColors[1][idx]++;
    }

    public void increaseSigns(int... idx) {
        for (int i : idx) this.signs[i]++;
    }

    public int[] getSigns() {
        return signs;
    }

    public void increaseAllSignOccurrences(int count) {
        this.allSignOccurrences += count;
    }

    public int getAllSignOccurrences() {
        return allSignOccurrences;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getBlueOccurrences() {
        return this.rgbOccurrences[2];
    }

    public int getRedOccurrences() {
        return this.rgbOccurrences[0];
    }

    public int getGreenOccurrences() {
        return this.rgbOccurrences[1];
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
