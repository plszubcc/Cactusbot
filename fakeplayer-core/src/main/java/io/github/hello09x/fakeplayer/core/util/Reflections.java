package io.github.hello09x.fakeplayer.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.checkerframework.checker.units.qual.N;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Reflections {
   public Reflections() {
   }

   @Nullable
   public static Field getFirstFieldByType(@NotNull Class<?> clazz, @NotNull Class<?> fieldType, boolean includeStatic) {
      for (Field field : clazz.getDeclaredFields()) {
         if (!(includeStatic ^ Modifier.isStatic(field.getModifiers())) && field.getType() == fieldType) {
            field.setAccessible(true);
            return field;
         }
      }

      return null;
   }

   @N
   public static Field getFistFieldByTypeIncludeParent(@NotNull Class<?> clazz, @NotNull Class<?> fieldType) {
      for (Field field : clazz.getDeclaredFields()) {
         if (field.getType() == fieldType) {
            field.setAccessible(true);
            return field;
         }
      }

      Class<?> superclass = clazz.getSuperclass();
      return superclass != null && superclass != Object.class ? getFistFieldByTypeIncludeParent(superclass, fieldType) : null;
   }

   @Nullable
   public static Field getFirstFieldByAssignFromType(@NotNull Class<?> clazz, @NotNull Class<?> fieldType, boolean includeStatic) {
      for (Field field : clazz.getDeclaredFields()) {
         if (!(includeStatic ^ Modifier.isStatic(field.getModifiers())) && fieldType.isAssignableFrom(field.getType())) {
            field.setAccessible(true);
            return field;
         }
      }

      return null;
   }
}
