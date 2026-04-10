package io.github.hello09x.devtools.storage;

import com.google.common.base.Throwables;
import io.github.hello09x.devtools.storage.value.AbstractValueWrapper;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.codehaus.plexus.util.IOUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JSONPersistentDataContainer implements PersistentDataContainer {

    private static final JSONHandler HANDLER = new JSONHandler();
    private final transient Plugin plugin;
    private final transient String filename;
    private final Map<String, AbstractValueWrapper<?>> data = new HashMap();

    public JSONPersistentDataContainer(@NotNull JavaPlugin plugin, @NotNull String filename) {
        super();
        this.plugin = plugin;
        this.filename = filename;
        File file = new File(plugin.getDataFolder(), filename);

        if (file.exists() && file.isFile()) {
            try {
                this.readFromFile(file, false);
            } catch (IOException ioexception) {
                Logger logger = plugin.getLogger();
                String s1 = file.getPath();

                logger.severe("Failed to read persistent data file: " + s1 + "\n" + Throwables.getStackTraceAsString(ioexception));
            }
        }

    }

    public static @NotNull JSONPersistentDataContainer deserializeFromJSON(@NotNull String json) {
        return (JSONPersistentDataContainer) JSONPersistentDataContainer.HANDLER.getGson().fromJson(json, JSONPersistentDataContainer.class);
    }

    public <T, Z> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z value) {
        this.data.put(key.toString(), AbstractValueWrapper.of(type, value));
    }

    public <T, Z> boolean has(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
        return this.get(key, type) != null;
    }

    public <T, Z> @Nullable Z get(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type) {
        return (Z) Optional.ofNullable((AbstractValueWrapper) this.data.get(key.toString())).map(AbstractValueWrapper::getValue).filter((valuex) -> {
            return type.getComplexType().isAssignableFrom(valuex.getClass());
        }).map((valuex) -> {
            return type.getComplexType().cast(valuex);
        }).orElse((Object) null);
    }

    public <T, Z> @NotNull Z getOrDefault(@NotNull NamespacedKey key, @NotNull PersistentDataType<T, Z> type, @NotNull Z defaultValue) {
        return (Z) Optional.ofNullable(this.get(key, type)).orElse(defaultValue);
    }

    public @NotNull Set<NamespacedKey> getKeys() {
        return (Set) this.data.keySet().stream().map((keyx) -> {
            String[] parts = keyx.split(":");

            return parts.length != 2 ? null : new NamespacedKey(parts[0], parts[1]);
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public void remove(@NotNull NamespacedKey key) {
        this.data.remove(key.toString());
    }

    public boolean isEmpty() {
        return this.data.isEmpty();
    }

    public @NotNull PersistentDataAdapterContext getAdapterContext() {
        throw new UnsupportedOperationException("getAdapterContext");
    }

    public boolean has(@NotNull NamespacedKey key) {
        return this.data.containsKey(key.toString());
    }

    public @NotNull String serializeToJSON() {
        return JSONPersistentDataContainer.HANDLER.toJSON(this);
    }

    public void readFromBytes(byte @NotNull [] bytes, boolean clear) {
        if (clear) {
            this.data.clear();
        }

        Optional optional = Optional.ofNullable((JSONPersistentDataContainer) JSONPersistentDataContainer.HANDLER.fromJSON(new String(bytes, StandardCharsets.UTF_8), JSONPersistentDataContainer.class)).map(JSONPersistentDataContainer::getData);
        Map map = this.data;

        Objects.requireNonNull(this.data);
        optional.ifPresent(map::putAll);
    }

    public boolean readFromFile(@NotNull File file, boolean clear) throws IOException {
        if (clear) {
            this.data.clear();
        }

        if (file.exists() && file.isFile()) {
            Optional optional = Optional.ofNullable((JSONPersistentDataContainer) JSONPersistentDataContainer.HANDLER.fromFile(file, JSONPersistentDataContainer.class)).map(JSONPersistentDataContainer::getData);
            Map map = this.data;

            Objects.requireNonNull(this.data);
            optional.ifPresent(map::putAll);
            return true;
        } else {
            return false;
        }
    }

    public byte[] serializeToBytes() throws IOException {
        return this.serializeToJSON().getBytes(StandardCharsets.UTF_8);
    }

    public void clear() {
        this.data.clear();
    }

    public void saveFile() {
        File file = new File(this.plugin.getDataFolder(), this.filename);

        try (FileOutputStream out = new FileOutputStream(file)) {
            IOUtil.copy(this.serializeToBytes(), out);
        } catch (IOException ioexception) {
            Logger logger = this.plugin.getLogger();
            String s = file.getPath();

            logger.severe("Failed to save persistent data file: " + s + "\n" + Throwables.getStackTraceAsString(ioexception));
        }

    }

    private Map<String, AbstractValueWrapper<?>> getData() {
        return this.data;
    }
}
