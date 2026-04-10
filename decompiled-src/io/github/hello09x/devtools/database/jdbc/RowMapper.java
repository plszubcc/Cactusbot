package io.github.hello09x.devtools.database.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RowMapper<T> {

    @Nullable
    T mapRow(@NotNull ResultSet resultset, int i) throws SQLException;
}
