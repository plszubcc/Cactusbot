package io.github.hello09x.devtools.database.jdbc.rowmapper;

import io.github.hello09x.devtools.database.jdbc.JdbcUtils;
import io.github.hello09x.devtools.database.jdbc.RowMapper;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MapRowMapper implements RowMapper<Map<String, Object>> {

    public MapRowMapper() {
        super();
    }

    public @Nullable Map<String, Object> mapRow(@NotNull ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columnCount = md.getColumnCount();
        LinkedHashMap<String, Object> result = new LinkedHashMap();

        for (int i = 0; i < columnCount; ++i) {
            String column = md.getColumnName(i + 1);

            result.put(JdbcUtils.lookupColumnName(md, i + 1), rs.getObject(column));
        }

        return result;
    }
}
