package com.susen36.caerulaarbor.util;

public class MathUtils {

    private MathUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean isMultipleOf(double value, double divisor) {
        return value % divisor == 0;
    }

    public static double getCosine(double a1, double a2, double b1, double b2) {
        double dotProd;
        double norm;
        double norm1;
        dotProd = a1 * a2 + b1 * b2;
        norm = Math.pow(a1 * a1 + b1 * b1, 0.5);
        norm1 = Math.pow(a2 * a2 + b2 * b2, 0.5);
        if (norm * norm1 == 0) {
            return 1;
        }
        return dotProd / (norm * norm1);
    }

    public static double minOfFour(double a1, double a2, double a3, double a4) {
        return Math.min(Math.min(a1, a2), Math.min(a3, a4));
    }
}
