package io.github.hello09x.devtools.storage.type;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public interface JSONPersistentDataType {

    PersistentDataType<String[], String[]> STRING_ARRAY = new JSONPersistentDataType.JSONComplexPersistentDataType(String[].class);
    PersistentDataType<StringObjectMap, StringObjectMap> MAP = new JSONPersistentDataType.JSONComplexPersistentDataType(StringObjectMap.class);

    public static class JSONComplexPersistentDataType<Z> implements PersistentDataType<Z, Z> {

        private final Class<Z> type;

        public JSONComplexPersistentDataType(Class<Z> type) {
            super();
            this.type = type;
        }

        private JSONComplexPersistentDataType(Z represent) {
            super();
            this.type = represent.getClass();
        }

        public @NotNull Class<Z> getPrimitiveType() {
            return this.type;
        }

        public @NotNull Class<Z> getComplexType() {
            return this.type;
        }

        public @NotNull Z toPrimitive(@NotNull Z complex, @NotNull PersistentDataAdapterContext context) {
            return complex;
        }

        public @NotNull Z fromPrimitive(@NotNull Z primitive, @NotNull PersistentDataAdapterContext context) {
            return primitive;
        }
    }
}
