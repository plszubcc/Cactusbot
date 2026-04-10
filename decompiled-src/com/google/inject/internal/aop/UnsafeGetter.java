package com.google.inject.internal.aop;

import java.lang.reflect.Field;
import sun.misc.Unsafe;

final class UnsafeGetter {

    private UnsafeGetter() {
        super();
    }

    static Unsafe getUnsafe() throws ReflectiveOperationException {
        try {
            return Unsafe.getUnsafe();
        } catch (SecurityException securityexception) {
            Class<Unsafe> k = Unsafe.class;

            for (Field f : k.getDeclaredFields()) {
                f.setAccessible(true);
                Object x = f.get((Object) null);

                if (k.isInstance(x)) {
                    return (Unsafe) k.cast(x);
                }
            }

            throw new NoSuchFieldError("the Unsafe");
        }
    }
}
