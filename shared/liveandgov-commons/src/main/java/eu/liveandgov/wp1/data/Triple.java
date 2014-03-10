package eu.liveandgov.wp1.data;

/**
 * <p>Value triple</p>
 * Created by Lukas Härtel on 09.02.14.
 *
 * @param <T> Type of the left object
 * @param <U> Type of the center object
 * @param <V> Type of the right object
 */
public final class Triple<T, U, V> implements Comparable<Triple<T, U, V>> {
    /**
     * Value of the left object
     */
    public final T left;

    /**
     * Value of the center object
     */
    public final U center;

    /**
     * Value of the right object
     */
    public final V right;

    /**
     * Creates a new instance with the given values
     *
     * @param left   Value of the left object
     * @param center Value of the center object
     * @param right  Value of the right object
     */
    public Triple(T left, U center, V right) {
        this.left = left;
        this.center = center;
        this.right = right;
    }

    /**
     * Creates a new instance with the given values
     *
     * @param left   Value of the left object
     * @param center Value of the center object
     * @param right  Value of the right object
     * @param <T>    Type of the left object
     * @param <U>    Type of the center object
     * @param <V>    Type of the right object
     * @return Returns the new instance
     */
    public static <T, U, V> Triple<T, U, V> create(T left, U center, V right) {
        return new Triple<T, U, V>(left, center, right);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Triple triple = (Triple) o;

        if (center != null ? !center.equals(triple.center) : triple.center != null) return false;
        if (left != null ? !left.equals(triple.left) : triple.left != null) return false;
        if (right != null ? !right.equals(triple.right) : triple.right != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (center != null ? center.hashCode() : 0);
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Triple<T, U, V> o) {
        if (this == o) return 0;
        if (o == null) return -1;

        if (left instanceof Comparable) {
            final int pl = ((Comparable) left).compareTo(o.left);

            if (pl != 0) return pl;
        }

        if (center instanceof Comparable) {
            final int pc = ((Comparable) center).compareTo(o.center);

            if (pc != 0) return pc;
        }

        if (right instanceof Comparable) {
            final int pr = ((Comparable) right).compareTo(o.right);

            if (pr != 0) return pr;
        }

        return 0;
    }

    /**
     * Flips the triple
     *
     * @return Returns the flipped triple
     */
    public Triple<V, U, T> flip() {
        return new Triple<V, U, T>(right, center, left);
    }

    @Override
    public String toString() {
        return "(" + left + ", " + center + ", " + right + ")";
    }
}
