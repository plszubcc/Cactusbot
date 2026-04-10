package io.github.hello09x.devtools.database.jdbc;

import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public interface KeyHolder {

    @Nullable
    Number getKey();

    <T> @Nullable T getKeyAs(Class<T> oclass);

    @Nullable
    Map<String, Object> getKeys();

    List<Map<String, Object>> getKeyList();
}
