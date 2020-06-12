import java.util.function.BiFunction;

public class Pixel {

    private int red;
    private int green;
    private int blue;

    public Pixel(int red, int green, int blue) {
        this.red = checkCorrectRange(red, Colors.RED);
        this.green = checkCorrectRange(green, Colors.GREEN);
        this.blue = checkCorrectRange(blue, Colors.BLUE);
    }


    private int checkCorrectRange(int value, Colors color) {
        if (value < 0 || value > 255)
            throw new IllegalArgumentException(
                    String.format("[%s] Required range is [0,255] for color %s current value = %d", this.getClass().getName(), color.name(), value));
        return value;
    }

    public Integer[] pixelColorAvg(Pixel pixel) {
        return createColorResult(pixel, (x, y) -> (x + y) / 2);
    }

    public Integer[] pixelColorDeviation(Pixel pixel) {
        return createColorResult(pixel, (x, y) -> (x - y) / 2);
    }

    private Integer[] createColorResult(Pixel pixel, BiFunction<Integer, Integer, Integer> func) {
        return new Integer[]{
                func.apply(this.red, pixel.red),
                func.apply(this.green, pixel.green),
                func.apply(this.blue, pixel.blue)
        };
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    private enum Colors {
        RED,
        GREEN,
        BLUE
    }
}
