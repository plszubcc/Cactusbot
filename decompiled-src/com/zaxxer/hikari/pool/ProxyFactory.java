package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.util.FastList;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public final class ProxyFactory {

    private ProxyFactory() {
        super();
    }

    static ProxyConnection getProxyConnection(PoolEntry poolentry, Connection connection, FastList<Statement> fastlist, ProxyLeakTask proxyleaktask, boolean flag, boolean flag1) {
        return new HikariProxyConnection(poolentry, connection, fastlist, proxyleaktask, flag, flag1);
    }

    static Statement getProxyStatement(ProxyConnection proxyconnection, Statement statement) {
        return new HikariProxyStatement(proxyconnection, statement);
    }

    static CallableStatement getProxyCallableStatement(ProxyConnection proxyconnection, CallableStatement callablestatement) {
        return new HikariProxyCallableStatement(proxyconnection, callablestatement);
    }

    static PreparedStatement getProxyPreparedStatement(ProxyConnection proxyconnection, PreparedStatement preparedstatement) {
        return new HikariProxyPreparedStatement(proxyconnection, preparedstatement);
    }

    static ResultSet getProxyResultSet(ProxyConnection proxyconnection, ProxyStatement proxystatement, ResultSet resultset) {
        return new HikariProxyResultSet(proxyconnection, proxystatement, resultset);
    }

    static DatabaseMetaData getProxyDatabaseMetaData(ProxyConnection proxyconnection, DatabaseMetaData databasemetadata) {
        return new HikariProxyDatabaseMetaData(proxyconnection, databasemetadata);
    }
}
