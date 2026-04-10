package io.github.hello09x.devtools.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.hello09x.devtools.storage.value.AbstractValueWrapper;
import io.github.hello09x.devtools.storage.value.ByteArrayValueWrapper;
import io.github.hello09x.devtools.storage.value.ByteValueWrapper;
import io.github.hello09x.devtools.storage.value.DoubleValueWrapper;
import io.github.hello09x.devtools.storage.value.FloatValueWrapper;
import io.github.hello09x.devtools.storage.value.IntArrayValueWrapper;
import io.github.hello09x.devtools.storage.value.IntValueWrapper;
import io.github.hello09x.devtools.storage.value.LongArrayValueWrapper;
import io.github.hello09x.devtools.storage.value.LongValueWrapper;
import io.github.hello09x.devtools.storage.value.MapValueWrapper;
import io.github.hello09x.devtools.storage.value.ShortValueWrapper;
import io.github.hello09x.devtools.storage.value.StringArrayValueWrapper;
import io.github.hello09x.devtools.storage.value.StringValueWrapper;
import io.github.hello09x.devtools.storage.value.TagContainerArrayValueWrapper;
import io.github.hello09x.devtools.storage.value.TagContainerValueWrapper;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JSONHandler {

    private final Gson gson = (new GsonBuilder()).registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of(AbstractValueWrapper.class, "type").registerSubtype(ByteValueWrapper.class, "byte").registerSubtype(ShortValueWrapper.class, "short").registerSubtype(IntValueWrapper.class, "int").registerSubtype(LongValueWrapper.class, "long").registerSubtype(FloatValueWrapper.class, "float").registerSubtype(DoubleValueWrapper.class, "double").registerSubtype(StringValueWrapper.class, "string").registerSubtype(IntArrayValueWrapper.class, "int[]").registerSubtype(LongArrayValueWrapper.class, "long[]").registerSubtype(ByteArrayValueWrapper.class, "byte[]").registerSubtype(StringArrayValueWrapper.class, "string[]").registerSubtype(MapValueWrapper.class, "map[string]object").registerSubtype(TagContainerValueWrapper.class, "tag").registerSubtype(TagContainerArrayValueWrapper.class, "tag[]")).create();

    public JSONHandler() {
        super();
    }

    @Contract(pure = true, value = "null -> null")
    public String toJSON(@Nullable Object object) {
        return this.gson.toJson(object);
    }

    @Contract(pure = true, value = "null, _ -> null")
    public <T> @Nullable T fromJSON(@Nullable String json, Class<T> type) {
        return (T) this.gson.fromJson(json, type);
    }

    public <T> @Nullable T fromFile(@NotNull File file, Class<T> type) throws IOException {
        try (FileReader reader = new FileReader(file, StandardCharsets.UTF_8)) {
            return (T) this.gson.fromJson(reader, type);
        }
    }

    public Gson getGson() {
        return this.gson;
    }
}
