package io.github.hello09x.fakeplayer.core.util;

public class Mth {
   public Mth() {
   }

   public static double floor(double num, double base) {
      return num % base == 0.0 ? num : Math.floor(num / base) * base;
   }

   public static float clamp(float value, float min, float max) {
      return value < min ? min : Math.min(value, max);
   }
}
