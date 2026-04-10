package io.github.hello09x.devtools.database.jdbc;

import io.github.hello09x.devtools.database.exception.TooManyResultException;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.stream.Stream;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JdbcUtils {

    public JdbcUtils() {
        super();
    }

    public static void closeStatement(@NotNull Plugin plugin, @Nullable Statement stm) {
        if (stm != null) {
            try {
                stm.close();
            } catch (Throwable throwable) {
                plugin.getLogger().warning("Failed to close statement: " + throwable.getMessage());
            }

        }
    }

    public static <T> @Nullable T nullableSingleResult(@Nullable Collection<T> results) {
        if (results != null && !results.isEmpty()) {
            if (results.size() > 1) {
                throw new TooManyResultException("Too many results: " + results.size());
            } else {
                return (T) results.iterator().next();
            }
        } else {
            return null;
        }
    }

    public static <T> @Nullable T nullableSingleResult(@Nullable Stream<T> results) throws SQLException {
        return (T) (results == null ? null : nullableSingleResult(results.limit(2L).toList()));
    }

    public static @NotNull String lookupColumnName(@NotNull ResultSetMetaData resultSetMetaData, int columnIndex) throws SQLException {
        String name = resultSetMetaData.getColumnLabel(columnIndex);

        if (!name.isEmpty()) {
            name = resultSetMetaData.getColumnName(columnIndex);
        }

        return name;
    }
}
