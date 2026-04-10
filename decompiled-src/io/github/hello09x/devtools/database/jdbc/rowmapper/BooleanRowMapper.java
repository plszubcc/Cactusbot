package io.github.hello09x.devtools.database.jdbc.rowmapper;

import io.github.hello09x.devtools.database.jdbc.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BooleanRowMapper implements RowMapper<Boolean> {

    public BooleanRowMapper() {
        super();
    }

    public @Nullable Boolean mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
        return rs.getBoolean(1);
    }
}
