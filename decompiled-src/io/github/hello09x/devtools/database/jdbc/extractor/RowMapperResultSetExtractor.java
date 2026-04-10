package io.github.hello09x.devtools.database.jdbc.extractor;

import io.github.hello09x.devtools.database.jdbc.ResultSetExtractor;
import io.github.hello09x.devtools.database.jdbc.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RowMapperResultSetExtractor<T> implements ResultSetExtractor<List<T>> {

    private final RowMapper<T> rowMapper;

    public RowMapperResultSetExtractor(RowMapper<T> rowMapper) {
        super();
        this.rowMapper = rowMapper;
    }

    public @Nullable List<T> extractData(@NotNull ResultSet rs) throws SQLException {
        ArrayList<T> results = new ArrayList();
        int rowNum = 0;

        while (rs.next()) {
            results.add(this.rowMapper.mapRow(rs, rowNum++));
        }

        return results;
    }
}
