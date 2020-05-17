public class Pixel {

    private double red;
    private double green;
    private double blue;

    protected Pixel() {
    }

    public Pixel(double red, double green, double blue) {
        this.red = checkCorrectRange(red, Colors.RED);
        this.green = checkCorrectRange(green, Colors.GREEN);
        this.blue = checkCorrectRange(blue, Colors.BLUE);
    }


    private double checkCorrectRange(double value, Colors color) {
        if (value < 0D || value > 255D)
            throw new IllegalArgumentException(String.format("[%s] Required range is [0,255] for color %s current value = %f", this.getClass().getName(), color.name(), value));
        return value;
    }

    public void setRed(double red) {
        this.red = checkCorrectRange(red, Colors.RED);
    }

    public void setGreen(double green) {
        this.green = checkCorrectRange(green, Colors.GREEN);
    }

    public void setBlue(double blue) {
        this.blue = checkCorrectRange(blue, Colors.BLUE);
    }

    public double getRed() {
        return red;
    }

    public double getGreen() {
        return green;
    }

    public double getBlue() {
        return blue;
    }

//    public double getDistanceToPixel(Pixel pixel) {
//        return (pixel != null) ? Math.sqrt(
//                Math.pow(this.red - pixel.getRed(), 2.0) +
//                        Math.pow(this.green - pixel.getGreen(), 2.0) +
//                        Math.pow(this.blue - pixel.getBlue(), 2.0)) : -1;
//    }

    public double getDistanceToPixel(Pixel pixel) {
        return Math.pow(this.red - pixel.getRed(), 2.0) +
                Math.pow(this.green - pixel.getGreen(), 2.0) +
                Math.pow(this.blue - pixel.getBlue(), 2.0);
    }

    public Pixel getDisorderedPixel(double epsilonValue) {
        return new Pixel(this.red * epsilonValue, this.green * epsilonValue, this.blue * epsilonValue);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + ((Double) red).hashCode() +
                ((Double) green).hashCode() +
                ((Double) blue).hashCode();
    }

    @Override
    public String toString() {
        return "Pixel" + String.format("[%1.0f,%1.0f,%1.0f]", red, green, blue);
    }

    private enum Colors {
        RED,
        GREEN,
        BLUE
    }
}
