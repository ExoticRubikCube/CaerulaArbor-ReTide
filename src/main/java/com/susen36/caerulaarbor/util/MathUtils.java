package com.susen36.caerulaarbor.util;

public class MathUtils {

    private MathUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 计算由 {@code (a1, b1)} 和 {@code (a2, b2)} 定义的两个二维向量夹角的余弦值。
     * <p>
     * 如果任一向量为零向量，此方法将安全地返回 {@code 1.0} 以防止除以零。
     *
     * @param a1 第一个向量的 x 分量
     * @param a2 第二个向量的 x 分量
     * @param b1 第一个向量的 y 分量
     * @param b2 第二个向量的 y 分量
     * @return 两个向量夹角的余弦值，范围在 -1.0 到 1.0 之间
     */
    public static double getCosine(double a1, double a2, double b1, double b2) {
        double dotProd = a1 * a2 + b1 * b2;
        double norm = Math.hypot(a1, b1);
        double norm1 = Math.hypot(a2, b2);

        if (norm == 0 || norm1 == 0) {
            return 1.0;
        }
        return dotProd / (norm * norm1);
    }

}