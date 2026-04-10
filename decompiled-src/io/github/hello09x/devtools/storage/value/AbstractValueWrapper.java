package io.github.hello09x.devtools.storage.value;

import io.github.hello09x.devtools.storage.JSONPersistentDataContainer;
import io.github.hello09x.devtools.storage.type.StringObjectMap;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractValueWrapper<T> {

    protected final @Nullable T value;

    public static <Z> @NotNull AbstractValueWrapper<?> of(@NotNull PersistentDataType<?, Z> type, @Nullable Z value) {
        Class<Z> complexType = type.getComplexType();

        if (complexType != Boolean.TYPE && complexType != Boolean.class) {
            if (complexType != Byte.TYPE && complexType != Byte.class) {
                if (complexType != Integer.TYPE && complexType != Integer.class) {
                    if (complexType != Short.TYPE && complexType != Short.class) {
                        if (complexType != Long.TYPE && complexType != Long.class) {
                            if (complexType != Float.TYPE && complexType != Float.class) {
                                if (complexType != Double.TYPE && complexType != Double.class) {
                                    if (complexType == String.class) {
                                        return new StringValueWrapper((String) value);
                                    } else if (complexType == String[].class) {
                                        return new StringArrayValueWrapper((String[]) value);
                                    } else if (complexType == byte[].class) {
                                        return new ByteArrayValueWrapper((byte[]) value);
                                    } else if (complexType == int[].class) {
                                        return new IntArrayValueWrapper((int[]) value);
                                    } else if (complexType == long[].class) {
                                        return new LongArrayValueWrapper((long[]) value);
                                    } else if (complexType == TagContainerValueWrapper.class) {
                                        return new TagContainerValueWrapper((JSONPersistentDataContainer) value);
                                    } else if (complexType == JSONPersistentDataContainer[].class) {
                                        return new TagContainerArrayValueWrapper((JSONPersistentDataContainer[]) value);
                                    } else if (complexType == StringObjectMap.class) {
                                        return new MapValueWrapper((StringObjectMap) value);
                                    } else {
                                        throw new IllegalArgumentException("unsupported type: " + type.getPrimitiveType());
                                    }
                                } else {
                                    return new DoubleValueWrapper((Double) value);
                                }
                            } else {
                                return new FloatValueWrapper((Float) value);
                            }
                        } else {
                            return new LongValueWrapper((Long) value);
                        }
                    } else {
                        return new ShortValueWrapper((Short) value);
                    }
                } else {
                    return new IntValueWrapper((Integer) value);
                }
            } else {
                return new ByteValueWrapper((Byte) value);
            }
        } else {
            return new BooleanValueWrapper((Boolean) value);
        }
    }

    public AbstractValueWrapper(@Nullable T value) {
        super();
        this.value = value;
    }

    public @Nullable T getValue() {
        return this.value;
    }
}
