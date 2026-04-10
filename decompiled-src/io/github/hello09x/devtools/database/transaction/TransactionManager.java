package io.github.hello09x.devtools.database.transaction;

import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.sql.DataSource;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TransactionManager {

    private final Plugin plugin;
    private final DataSource dataSource;
    private static final Map<Plugin, ThreadLocal<TransactionStatus>> TXS = new ConcurrentHashMap();

    public TransactionManager(@NotNull Plugin plugin, @NotNull DataSource dataSource) {
        super();
        this.plugin = plugin;
        this.dataSource = dataSource;
    }

    public @NotNull TransactionStatus getTransactionStatus() throws SQLException {
        TransactionStatus tx = getCurrentTransaction(this.plugin);

        if (tx == null) {
            tx = new TransactionStatus(this.plugin, this.dataSource);
            ((ThreadLocal) TransactionManager.TXS.computeIfAbsent(this.plugin, (kx) -> {
                return new ThreadLocal();
            })).set(tx);
        }

        return tx;
    }

    public static @Nullable TransactionStatus getCurrentTransaction(@NotNull Plugin plugin) {
        return (TransactionStatus) Optional.ofNullable((ThreadLocal) TransactionManager.TXS.get(plugin)).map(ThreadLocal::get).orElse((Object) null);
    }

    public void commit(@NotNull TransactionStatus status) throws SQLException {
        status.commit();
    }

    public void rollback(@NotNull TransactionStatus status) throws SQLException {
        status.rollback();
    }
}
