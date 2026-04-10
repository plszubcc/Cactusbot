package io.github.hello09x.devtools.database.jdbc;

import io.github.hello09x.devtools.database.exception.DataAccessException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class GeneratedKeyHolder implements KeyHolder {

    private final List<Map<String, Object>> keyList;

    public GeneratedKeyHolder() {
        super();
        this.keyList = new ArrayList(1);
    }

    public GeneratedKeyHolder(List<Map<String, Object>> keyList) {
        super();
        this.keyList = keyList;
    }

    public @Nullable Number getKey() {
        return (Number) this.getKeyAs(Number.class);
    }

    public <T> @Nullable T getKeyAs(Class<T> keyType) {
        if (this.keyList.isEmpty()) {
            return null;
        } else if (this.keyList.size() <= 1 && ((Map) this.keyList.get(0)).size() <= 1) {
            Iterator<Object> itr = ((Map) this.keyList.get(0)).values().iterator();

            if (itr.hasNext()) {
                Object key = itr.next();

                if (!keyType.isInstance(key)) {
                    throw new DataAccessException("The generated key type is not supported. Unable to case [%s] to [%s].".formatted(key != null ? key.getClass().getName() : null, keyType.getName()));
                } else {
                    return (T) keyType.cast(key);
                }
            } else {
                throw new DataAccessException("Unable to retrieve the generated key. The generated key does not contain any keys.");
            }
        } else {
            throw new DataAccessException("The getKey method should only be used when a single key is returned. The current key entry contains multi keys: " + this.keyList);
        }
    }

    public @Nullable Map<String, Object> getKeys() {
        if (this.keyList.isEmpty()) {
            return null;
        } else if (this.keyList.size() > 1) {
            throw new DataAccessException("The getKeys method should only be used when keys from a single row are returned. The current key list contains keys for multiple rows: " + this.keyList);
        } else {
            return (Map) this.keyList.get(0);
        }
    }

    public List<Map<String, Object>> getKeyList() {
        return this.keyList;
    }
}
