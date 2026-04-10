package com.zaxxer.hikari.pool;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;

public abstract class ProxyCallableStatement extends ProxyPreparedStatement implements CallableStatement {

    protected ProxyCallableStatement(ProxyConnection connection, CallableStatement statement) {
        super(connection, (PreparedStatement) statement);
    }
}
