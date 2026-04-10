package io.github.hello09x.devtools.database.jdbc.rowmapper;

import io.github.hello09x.devtools.database.jdbc.GeneratedKeyHolder;
import io.github.hello09x.devtools.database.jdbc.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeneratedKeyHolderRowMapper implements RowMapper<GeneratedKeyHolder> {

    public GeneratedKeyHolderRowMapper() {
        super();
    }

    public @Nullable GeneratedKeyHolder mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
        return null;
    }
}
