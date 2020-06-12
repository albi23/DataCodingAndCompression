import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public class DictionaryTree<T extends Integer> {

    private class Node {
        int value;
        String code;
        Node left;
        Node right;

        public Node(String code, int value) {
            this.code = code;
            this.value = value;
            this.left = null;
            this.right = null;
        }

    }

    private final Random random = new Random(new Date().getTime());
    private final Node root;
    private final int dicHeight;

    public DictionaryTree(int dicHeight, List<Integer> values) {
        this.root = new Node("", 127);
        this.dicHeight = dicHeight;
        this.setAverageDistance(root, values.stream(),values.size());
        createNode(root, values);
    }

    private void setAverageDistance(Node node, Stream<Integer> values, int size) {
        final Integer value = values.reduce(0, Integer::sum);
        if (size > 0) node.value = value / size;
    }

    private void createNode(Node node, List<Integer> values) {
        if (node.code.length() > this.dicHeight) return;

        node.left = new Node(node.code.concat("0"), node.value);
        node.right = new Node(node.code.concat("1"), node.value);

        do {
            node.right.value += random.nextInt(5) - 2;
        } while (node.left.value == node.right.value);

        List<Integer> leftValues = new ArrayList<>();
        List<Integer> rightValues = new ArrayList<>();

        int valueLeftPrev, valueRightPrev;
        do {
            valueLeftPrev = node.left.value;
            valueRightPrev = node.right.value;
            leftValues.clear();
            rightValues.clear();

            values.forEach(val -> {
                if (Math.abs(val - node.left.value) < Math.abs(val - node.right.value)) {
                    leftValues.add(val);
                } else {
                    rightValues.add(val);
                }
            });
            setAverageDistance(node.left, leftValues.stream(), leftValues.size());
            setAverageDistance(node.right, rightValues.stream(), rightValues.size());
        } while ((node.left.value != valueLeftPrev || node.right.value != valueRightPrev));

        this.createNode(node.left, leftValues);
        this.createNode(node.right, rightValues);
    }

    public int getValue(String code) {
        Node node = this.root;
        for (int i = 0; node.left != null && i < code.length(); i++) {
            node = (node.left.code.charAt(i) == code.charAt(i)) ? node.left : node.right;
        }
        return node.value;
    }

    public String getCode(int value) {
        Node node = this.root;

        while (node.left != null) {
            if (Math.abs(value - node.left.value) < Math.abs(value - node.right.value)) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        return node.code;
    }
}
