package azakban.utils;

public class Utils {


    /**
     * Equivalent to Object.equals except that it handles nulls. If a and b are both null, true is
     * returned.
     */
    public static boolean equals(final Object a, final Object b) {
        if (a == null || b == null) {
            return a == b;
        }

        return a.equals(b);
    }

    /**
     * Return the object if it is non-null, otherwise throw an exception
     *
     * @param <T> The type of the object
     * @param t The object
     * @return The object if it is not null
     * @throws IllegalArgumentException if the object is null
     */
    public static <T> T nonNull(final T t) {
        if (t == null) {
            throw new IllegalArgumentException("Null value not allowed.");
        } else {
            return t;
        }
    }
}
