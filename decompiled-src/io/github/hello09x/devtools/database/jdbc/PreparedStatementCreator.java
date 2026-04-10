package io.github.hello09x.devtools.database.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.jetbrains.annotations.NotNull;

public interface PreparedStatementCreator {

    @NotNull
    PreparedStatement createPreparedStatement(Connection connection) throws SQLException;
}
