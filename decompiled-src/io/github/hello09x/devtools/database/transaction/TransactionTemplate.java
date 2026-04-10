package io.github.hello09x.devtools.database.transaction;

import com.google.common.base.Throwables;
import java.lang.reflect.UndeclaredThrowableException;
import java.sql.SQLException;
import java.util.function.Supplier;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class TransactionTemplate {

    private final Plugin plugin;
    private final TransactionManager transactionManager;

    public TransactionTemplate(@NotNull Plugin plugin, @NotNull TransactionManager transactionManager) {
        super();
        this.plugin = plugin;
        this.transactionManager = transactionManager;
    }

    public <T> @UnknownNullability T execute(@NotNull Supplier<T> execution) {
        TransactionStatus tx;

        try {
            tx = this.transactionManager.getTransactionStatus();
        } catch (SQLException sqlexception) {
            throw new UndeclaredThrowableException(sqlexception, "Failed to get transaction");
        }

        T result;

        try {
            result = (T) execution.get();
        } catch (Throwable throwable) {
            this.rollbackOnException(tx);
            throw throwable;
        }

        try {
            tx.commit();
            return result;
        } catch (Throwable throwable1) {
            throw new UndeclaredThrowableException(throwable1, "Failed to commit transaction: \n" + Throwables.getStackTraceAsString(throwable1));
        }
    }

    private void rollbackOnException(@NotNull TransactionStatus tx) {
        try {
            tx.rollback();
        } catch (Throwable throwable) {
            throw new UndeclaredThrowableException(throwable, "Failed to rollback");
        }
    }
}
