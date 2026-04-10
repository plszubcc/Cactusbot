package io.github.hello09x.devtools.database.jdbc;

import io.github.hello09x.devtools.database.exception.DataAccessException;
import io.github.hello09x.devtools.database.jdbc.extractor.RowMapperResultSetExtractor;
import io.github.hello09x.devtools.database.jdbc.rowmapper.MapRowMapper;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

public class JdbcTemplate {

    private final Plugin plugin;
    private final DataSource dataSource;

    public JdbcTemplate(@NotNull Plugin plugin, @NotNull DataSource dataSource) {
        super();
        this.plugin = plugin;
        this.dataSource = dataSource;
    }

    public void execute(@NotNull String sql) {
        this.execute0((stmx) -> {
            stmx.execute(sql);
            return null;
        });
    }

    public <T> @UnknownNullability T query(@NotNull String sql, @NotNull ResultSetExtractor<T> extractor) {
        return (T) this.execute0((stmx) -> {
            ResultSet rs = stmx.executeQuery(sql);

            return extractor.extractData(rs);
        });
    }

    public <T> @UnknownNullability T query(@NotNull String sql, @NotNull ResultSetExtractor<T> extractor, @NotNull Object... args) {
        return (T) this.execute0((stmx) -> {
            ResultSet rs = stmx.executeQuery();

            return extractor.extractData(rs);
        }, sql, args);
    }

    public <T> @NotNull List<T> query(@NotNull String sql, @NotNull RowMapper<T> rowMapper) {
        return (List) this.query(sql, new RowMapperResultSetExtractor(rowMapper));
    }

    public <T> @NotNull List<T> query(@NotNull String sql, @NotNull RowMapper<T> rowMapper, Object... args) {
        return (List) this.query(sql, new RowMapperResultSetExtractor(rowMapper), args);
    }

    public <T> @Nullable T queryForObject(@NotNull String sql, @NotNull RowMapper<T> rowMapper) {
        List<T> results = (List) this.query(sql, new RowMapperResultSetExtractor(rowMapper));

        return (T) JdbcUtils.nullableSingleResult(results);
    }

    public <T> @Nullable T queryForObject(@NotNull String sql, @NotNull RowMapper<T> rowMapper, @NotNull Object... args) {
        List<T> results = (List) this.query(sql, new RowMapperResultSetExtractor(rowMapper), args);

        return (T) JdbcUtils.nullableSingleResult(results);
    }

    public @Nullable Map<String, Object> queryForMap(@NotNull String sql) throws SQLException {
        List<Map<String, Object>> results = (List) this.query(sql, new RowMapperResultSetExtractor(new MapRowMapper()));

        return (Map) JdbcUtils.nullableSingleResult(results);
    }

    public @Nullable Map<String, Object> queryForMap(@NotNull String sql, @NotNull Object... args) throws SQLException {
        List<Map<String, Object>> results = (List) this.query(sql, new RowMapperResultSetExtractor(new MapRowMapper()), args);

        return (Map) JdbcUtils.nullableSingleResult(results);
    }

    public @NotNull List<Map<String, Object>> queryForList(@NotNull String sql) {
        return (List) this.query(sql, new RowMapperResultSetExtractor(new MapRowMapper()));
    }

    public @NotNull List<Map<String, Object>> queryForList(@NotNull String sql, Object... args) {
        return (List) this.query(sql, new RowMapperResultSetExtractor(new MapRowMapper()), args);
    }

    public int update(@NotNull String sql) {
        return (Integer) this.execute0((stmx) -> {
            return stmx.executeUpdate(sql);
        });
    }

    public int update(@NotNull String sql, Object... args) {
        return (Integer) this.execute0(PreparedStatement::executeUpdate, sql, args);
    }

    public int update(@NotNull String sql, @NotNull KeyHolder keyHolder, Object... args) {
        return (Integer) this.execute0((conx) -> {
            return conx.prepareStatement(sql, 1);
        }, (stmx) -> {
            int rows = stmx.executeUpdate();
            List<Map<String, Object>> generatedKeys = (new RowMapperResultSetExtractor(new MapRowMapper())).extractData(stmx.getGeneratedKeys());

            if (generatedKeys != null) {
                keyHolder.getKeyList().addAll(generatedKeys);
            }

            return rows;
        }, args);
    }

    private <T> @UnknownNullability T execute0(@NotNull SQLExchanger<Statement, T> execution) {
        Connection con = DataSourceUtils.getConnection(this.plugin, this.dataSource);
        Statement stm = null;

        Object object;

        try {
            stm = con.createStatement();
            object = execution.exchange(stm);
        } catch (SQLException sqlexception) {
            throw new DataAccessException(sqlexception);
        } finally {
            JdbcUtils.closeStatement(this.plugin, stm);
            DataSourceUtils.releaseConnection(this.plugin, con);
        }

        return (T) object;
    }

    private <T> @UnknownNullability T execute0(@NotNull PreparedStatementCreator preparedStatementCreator, @NotNull SQLExchanger<PreparedStatement, T> execution, @NotNull Object... args) {
        Connection con = DataSourceUtils.getConnection(this.plugin, this.dataSource);
        PreparedStatement stm = null;

        Object object;

        try {
            stm = preparedStatementCreator.createPreparedStatement(con);

            for (int i = 1; i <= args.length; ++i) {
                stm.setObject(i, args[i - 1]);
            }

            object = execution.exchange(stm);
        } catch (SQLException sqlexception) {
            throw new DataAccessException(sqlexception);
        } finally {
            JdbcUtils.closeStatement(this.plugin, stm);
            DataSourceUtils.releaseConnection(this.plugin, con);
        }

        return (T) object;
    }

    private <T> @UnknownNullability T execute0(@NotNull SQLExchanger<PreparedStatement, T> execution, @NotNull String sql, @NotNull Object... args) {
        return (T) this.execute0((conx) -> {
            return conx.prepareStatement(sql);
        }, execution, args);
    }
}
