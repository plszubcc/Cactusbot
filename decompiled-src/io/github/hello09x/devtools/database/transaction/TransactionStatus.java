package io.github.hello09x.devtools.database.transaction;

import io.github.hello09x.devtools.database.exception.DataAccessException;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class TransactionStatus {

    private final Plugin plugin;
    private final DataSource dataSource;
    private Connection connection;

    public TransactionStatus(@NotNull Plugin plugin, @NotNull DataSource dataSource) {
        super();
        this.plugin = plugin;
        this.dataSource = dataSource;
    }

    public @NotNull Connection getConnection() {
        if (this.connection == null) {
            try {
                this.connection = this.dataSource.getConnection();
            } catch (SQLException sqlexception) {
                throw new DataAccessException(sqlexception);
            }
        }

        return this.connection;
    }

    public void rollback() throws SQLException {
        if (this.connection != null) {
            this.connection.rollback();
            this.doOnCompleted();
        }
    }

    public void commit() throws SQLException {
        if (this.connection != null) {
            this.connection.commit();
            this.doOnCompleted();
        }
    }

    private void doOnCompleted() throws SQLException {
        Connection conn = this.connection;

        this.connection = null;
        conn.close();
    }
}
