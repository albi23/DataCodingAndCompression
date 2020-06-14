public class MainCoding {

    /**
     * @Author Albert Piekielny
     *
     * <p>Usage java MainCoding flag filePath1 filePath2<p/>
     * <p>flag: code, decode, check, noise</p>
     * <ul>
     *     <li>java MainCoding code inputFile outputFile</li>
     *     <li>java MainCoding decode inputFile outputFile</li>
     *     <li>java MainCoding noise inputFile outputFile probability</li>
     *     <li>java MainCoding check inputFile inputFile2</li>
     * </ul>
     *
     */
    public static void main(String... args) throws Exception {

        if (args.length == 3 || args.length == 4) {
            HammingCoding hammingEncode;
            switch (args[0]) {
                case "code":
                    hammingEncode = new HammingEncode(args[1], args[2]);
                    break;
                case "decode":
                    hammingEncode = new HammingDecoding(args[1], args[2]);
                    break;
                case "check":
                    hammingEncode = new FileByteComparator(args[1], args[2]);
                    break;
                case "noise":
                    hammingEncode = new BytesNoise(args[1], args[2], Double.parseDouble(args[3]));
                    break;
                default:
                    throw new UnsupportedOperationException("Unrecognized flag");
            }
            hammingEncode.manageFiles();
        } else {
            throw new IllegalArgumentException("Usage java MainCoding flag filePath1 filePath2\n" +
                    " flag: code, decode, check, noise");
        }
    }
}
