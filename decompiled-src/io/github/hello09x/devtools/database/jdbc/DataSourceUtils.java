package io.github.hello09x.devtools.database.jdbc;

import io.github.hello09x.devtools.database.exception.DataAccessException;
import io.github.hello09x.devtools.database.transaction.TransactionManager;
import io.github.hello09x.devtools.database.transaction.TransactionStatus;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DataSourceUtils {

    public DataSourceUtils() {
        super();
    }

    public static @NotNull Connection getConnection(@NotNull Plugin plugin, @NotNull DataSource dataSource) {
        TransactionStatus tx = TransactionManager.getCurrentTransaction(plugin);

        return tx != null ? tx.getConnection() : fetchConnection(dataSource);
    }

    private static @NotNull Connection fetchConnection(@NotNull DataSource dataSource) {
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
        } catch (SQLException sqlexception) {
            throw new DataAccessException(sqlexception);
        }

        return connection;
    }

    public static void releaseConnection(@NotNull Plugin plugin, @Nullable Connection connection) {
        if (connection != null) {
            TransactionStatus tx = TransactionManager.getCurrentTransaction(plugin);

            if (tx == null || tx.getConnection() != connection) {
                try {
                    connection.close();
                } catch (Throwable throwable) {
                    plugin.getLogger().warning("Failed to close connection: " + connection);
                }

            }
        }
    }
}
