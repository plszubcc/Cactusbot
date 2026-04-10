package io.github.hello09x.devtools.database.jdbc.rowmapper;

import io.github.hello09x.devtools.database.jdbc.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IntegerRowMapper implements RowMapper<Integer> {

    public static final IntegerRowMapper instance = new IntegerRowMapper();

    public IntegerRowMapper() {
        super();
    }

    public @Nullable Integer mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
        return (Integer) rs.getObject(1, Integer.class);
    }
}
