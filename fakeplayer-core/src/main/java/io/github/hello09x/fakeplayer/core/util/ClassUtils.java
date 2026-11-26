package io.github.hello09x.fakeplayer.core.util;

public class ClassUtils {
   public ClassUtils() {
   }

   public static boolean isClassExists(String className) {
      try {
         Class.forName(className);
         return true;
      } catch (ClassNotFoundException var2) {
         return false;
      }
   }
}
