package io.github.hello09x.devtools.database.exception;

import org.jetbrains.annotations.Nullable;

public class DataAccessException extends RuntimeException {

    public DataAccessException(@Nullable String message) {
        super(message);
    }

    public DataAccessException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(@Nullable Throwable cause) {
        super(cause);
    }
}
