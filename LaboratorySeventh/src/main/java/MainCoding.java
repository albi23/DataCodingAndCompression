public class MainCoding {


    /**
     * Usage java MainCoding flag filePath1 filePath2
     * flag: code, decode, check, noise
     *
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
                case "noise":
                    hammingEncode = new BytesNoise(args[1], args[2], Double.parseDouble(args[3]));
                default:
                    throw new UnsupportedOperationException("Unrecognized flag");
            }
            hammingEncode.manageFiles();
        } else {
            System.out.println("Usage java MainCoding flag filePath1 filePath2\n" +
                    " flag: code, decode, check, noise");
        }
    }
}
