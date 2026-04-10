package io.github.hello09x.devtools.core.utils;

public class Mth {

    public static double floor(double num, double base) {
        return num % base == 0.0D ? num : Math.floor(num / base) * base;
    }

    public static int align(int num, int base) {
        return num % base == 0 ? num : num / base * base + base;
    }

    public static double clamp(double min, double max, double value) {
        return Math.min(max, Math.max(min, value));
    }

    public static int clamp(int min, int max, int value) {
        return Math.min(max, Math.max(min, value));
    }

    public static long clamp(long min, long max, long value) {
        return Math.min(max, Math.max(min, value));
    }

    private Mth() {
        super();
    }
}
