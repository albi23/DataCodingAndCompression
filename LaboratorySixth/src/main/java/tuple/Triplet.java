package tuple;

import java.io.Serializable;

public class Triplet<A, B, C> implements Serializable {
    private static final long serialVersionUID = -1866264451509483740L;
    private final A val0;
    private final B val1;
    private final C val2;

    public Triplet(A val0, B val1, C val2) {
        this.val0 = val0;
        this.val1 = val1;
        this.val2 = val2;
    }

    public static <X> Triplet<X, X, X> fromArray(X[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null");
        } else if (array.length != 3) {
            throw new IllegalArgumentException("Array must have exactly 3 elements in order to create a Triplet. Size is " + array.length);
        } else {
            return new Triplet(array[0], array[1], array[2]);
        }
    }

    public A getValue0() {
        return this.val0;
    }

    public B getValue1() {
        return this.val1;
    }

    public C getValue2() {
        return this.val2;
    }


}
