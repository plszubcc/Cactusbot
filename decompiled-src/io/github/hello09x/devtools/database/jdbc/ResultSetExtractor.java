package io.github.hello09x.devtools.database.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ResultSetExtractor<T> {

    @Nullable
    T extractData(@NotNull ResultSet resultset) throws SQLException;
}
