import com.sun.istack.internal.NotNull;
import tuple.Triplet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TGAEncoder {

    /**
     * Triplet<Red, Green,Blue>
     */
    private Map<String, Triplet<Integer, Integer, Integer>[]> passFilters;
    private Map<String, List<DictionaryTree<Integer>>> dictionaries;


    public void encode(@NotNull final String inFilePath, short quantizationBitAmount) throws IOException {
        Objects.requireNonNull(inFilePath, "Param inFilePath must be not null.");
        final byte[] bytes = TGAUtils.readFile(inFilePath);
        final Pixel[] pixelArray = TGAUtils.createPixelArray(bytes);
        createFilters(pixelArray);
        createDictionary(quantizationBitAmount);
        writeData(quantizationBitAmount, bytes);
    }

    private void writeData(short quantizationBitAmount, byte[] bytes) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(getOutFile())) {
            writeDictionaryToFile(quantizationBitAmount, fos);
            writeHeader(bytes, fos);
            writeCode(fos);
        }

    }

    private void createFilters(Pixel[] pixelArray) {
        final int size = pixelArray.length;
        this.passFilters = new HashMap<>();
        @SuppressWarnings("unchecked") final Triplet<Integer, Integer, Integer>[] low =
                (Triplet<Integer, Integer, Integer>[]) Array.newInstance(Triplet.class, size / 2);
        @SuppressWarnings("unchecked") final Triplet<Integer, Integer, Integer>[] height =
                (Triplet<Integer, Integer, Integer>[]) Array.newInstance(Triplet.class, size / 2);

        for (int i = 1, j = 0; i < size; i += 2, j++) {
            Triplet<Integer, Integer, Integer> lowPassValue = Triplet.fromArray(pixelArray[i].pixelColorAvg(pixelArray[i - 1]));
            Triplet<Integer, Integer, Integer> heightPassValue = Triplet.fromArray(pixelArray[i].pixelColorDeviation(pixelArray[i - 1]));
            low[j] = lowPassValue;
            height[j] = heightPassValue;
        }

        this.passFilters.put("low", low);
        this.passFilters.put("height", height);
    }

    private void createDictionary(short bitSize) {

        Function<Triplet<Integer, Integer, Integer>, Integer>[] func = new Function[]{
                x -> ((Triplet) x).getValue0(), x -> ((Triplet) x).getValue1(), x -> ((Triplet) x).getValue2()
        };
        this.dictionaries = new HashMap<>(2);
        dictionaries.put("low", new ArrayList<>(3));
        dictionaries.put("height", new ArrayList<>(3));

        passFilters.forEach((k, v) -> {
            Arrays.stream(func).forEach(mapper -> {
                final List<Integer> values = Arrays.stream(v).map(mapper).collect(Collectors.toList());
                dictionaries.get(k).add(new DictionaryTree<>(bitSize, values));
            });
        });
    }

    private void writeDictionaryToFile(short size, FileOutputStream fos) throws IOException {

        final List<DictionaryTree<Integer>> low = dictionaries.get("low");
        final List<DictionaryTree<Integer>> height = dictionaries.get("height");
        int maxValue = (int) Math.pow(2.0, size);
        fos.write(size & 0xFF);

        for (int i = 0; i < maxValue; i++) {
            final String code = Integer.toBinaryString(i).substring(8 - size);
            for (int j = 0; j < low.size(); j++) {
                fos.write(low.get(j).getValue(code));
                fos.write(height.get(j).getValue(code));
            }
        }
    }

    private void writeHeader(byte[] imageBuff, FileOutputStream fos) throws IOException {
        byte[] header = new byte[TGAUtils.TGA_IMAGE_HEADER];
        System.arraycopy(imageBuff, 0, header, 0, TGAUtils.TGA_IMAGE_HEADER);
        fos.write(header);
    }

    private void writeCode(FileOutputStream fos) throws IOException {
        final StringBuilder sb = new StringBuilder();
        final Triplet<Integer, Integer, Integer>[] lowsFilters = this.passFilters.get("low");
        final Triplet<Integer, Integer, Integer>[] heightsFilters = this.passFilters.get("height");
        final List<DictionaryTree<Integer>> lowDic = dictionaries.get("low");
        final List<DictionaryTree<Integer>> heightDic = dictionaries.get("height");
        final int size = lowsFilters.length;

        for (int i = 0; i < size; i++) {
            sb.append(lowDic.get(0).getCode(lowsFilters[i].getValue0()));
            sb.append(heightDic.get(0).getCode(heightsFilters[i].getValue0()));

            sb.append(lowDic.get(1).getCode(lowsFilters[i].getValue1()));
            sb.append(heightDic.get(1).getCode(heightsFilters[i].getValue1()));

            sb.append(lowDic.get(2).getCode(lowsFilters[i].getValue2()));
            sb.append(heightDic.get(2).getCode(heightsFilters[i].getValue2()));
        }

        while (sb.length() % 8 != 0) sb.append("0");

        String code = sb.toString();
        final byte[] out = new byte[code.length() / 8];
        for (int i = 0; code.length() > 0; i++) {
            out[i] = (byte) Integer.parseInt(code.substring(0, 8), 2);
            code = code.substring(8);
        }
        fos.write(out);

    }

    private static String getOutFile() {
        DateFormat dateFormat = new SimpleDateFormat("_HH_mm_ss");
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        return System.getProperty("user.dir") + File.separator + "encoded" + dateFormat.format(currentDate) + ".bin";
    }
}
