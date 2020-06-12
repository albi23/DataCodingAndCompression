public class BitsUtils {

    public static int[] bitValueRepresentation(int x) {
        if (x < 0 || x > 255)
            throw new NumberFormatException("Value " + x + " out of range from input [0,255]");
        return fillArray(x, new int[Byte.SIZE]);
    }

    public static int[] intValueRepresentation(int x) {
        final int size = (x >= 0) ? (int) Math.ceil(Math.floor(Math.log(x) / Math.log(2))) : Integer.SIZE;
        return fillArray(x, new int[size]);
    }

    private static int[] fillArray(int x, int[] representation) {
        for (int i = representation.length - 1; i >= 0; i--) {
            representation[i] = x & 1;
            x >>= 1;
        }
        return representation;
    }

    public static byte toByteValue(int[] byteRepresentation) {
        int result = byteRepresentation[0];
        for (int i = 0, lastBit; i < byteRepresentation.length; i++) {
            lastBit = byteRepresentation[i] & 1;
            result = (result | lastBit) << 1;
        }
        return (byte) (result>>=1);
    }

}
