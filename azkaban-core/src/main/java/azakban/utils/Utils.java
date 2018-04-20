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
}
