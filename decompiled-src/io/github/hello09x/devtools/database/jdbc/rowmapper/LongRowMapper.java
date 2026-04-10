package io.github.hello09x.devtools.database.jdbc.rowmapper;

import io.github.hello09x.devtools.database.jdbc.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LongRowMapper implements RowMapper<Long> {

    public static final LongRowMapper instance = new LongRowMapper();

    public LongRowMapper() {
        super();
    }

    public @Nullable Long mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
        return (Long) rs.getObject(0, Long.class);
    }
}
