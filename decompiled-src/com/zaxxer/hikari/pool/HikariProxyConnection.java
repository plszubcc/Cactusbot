package com.zaxxer.hikari.pool;

import com.zaxxer.hikari.util.FastList;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.ShardingKey;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Wrapper;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public final class HikariProxyConnection extends ProxyConnection implements Wrapper, AutoCloseable, Connection {

    public Statement createStatement() throws SQLException {
        try {
            return super.createStatement();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public PreparedStatement prepareStatement(String s) throws SQLException {
        try {
            return super.prepareStatement(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public CallableStatement prepareCall(String s) throws SQLException {
        try {
            return super.prepareCall(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public String nativeSQL(String s) throws SQLException {
        try {
            return super.delegate.nativeSQL(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void setAutoCommit(boolean flag) throws SQLException {
        try {
            super.setAutoCommit(flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public boolean getAutoCommit() throws SQLException {
        try {
            return super.delegate.getAutoCommit();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void commit() throws SQLException {
        try {
            super.commit();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void rollback() throws SQLException {
        try {
            super.rollback();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public boolean isClosed() throws SQLException {
        try {
            return super.isClosed();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        try {
            return super.getMetaData();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void setReadOnly(boolean flag) throws SQLException {
        try {
            super.setReadOnly(flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public boolean isReadOnly() throws SQLException {
        try {
            return super.delegate.isReadOnly();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void setCatalog(String s) throws SQLException {
        try {
            super.setCatalog(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public String getCatalog() throws SQLException {
        try {
            return super.delegate.getCatalog();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void setTransactionIsolation(int i) throws SQLException {
        try {
            super.setTransactionIsolation(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public int getTransactionIsolation() throws SQLException {
        try {
            return super.delegate.getTransactionIsolation();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        try {
            return super.delegate.getWarnings();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void clearWarnings() throws SQLException {
        try {
            super.delegate.clearWarnings();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public Statement createStatement(int i, int j) throws SQLException {
        try {
            return super.createStatement(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public PreparedStatement prepareStatement(String s, int i, int j) throws SQLException {
        try {
            return super.prepareStatement(s, i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public CallableStatement prepareCall(String s, int i, int j) throws SQLException {
        try {
            return super.prepareCall(s, i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public Map getTypeMap() throws SQLException {
        try {
            return super.delegate.getTypeMap();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void setTypeMap(Map map) throws SQLException {
        try {
            super.delegate.setTypeMap(map);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void setHoldability(int i) throws SQLException {
        try {
            super.delegate.setHoldability(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public int getHoldability() throws SQLException {
        try {
            return super.delegate.getHoldability();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public Savepoint setSavepoint() throws SQLException {
        try {
            return super.delegate.setSavepoint();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public Savepoint setSavepoint(String s) throws SQLException {
        try {
            return super.delegate.setSavepoint(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void rollback(Savepoint savepoint) throws SQLException {
        try {
            super.rollback(savepoint);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        try {
            super.delegate.releaseSavepoint(savepoint);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public Statement createStatement(int i, int j, int k) throws SQLException {
        try {
            return super.createStatement(i, j, k);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public PreparedStatement prepareStatement(String s, int i, int j, int k) throws SQLException {
        try {
            return super.prepareStatement(s, i, j, k);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public CallableStatement prepareCall(String s, int i, int j, int k) throws SQLException {
        try {
            return super.prepareCall(s, i, j, k);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public PreparedStatement prepareStatement(String s, int i) throws SQLException {
        try {
            return super.prepareStatement(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public PreparedStatement prepareStatement(String s, int[] aint) throws SQLException {
        try {
            return super.prepareStatement(s, aint);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public PreparedStatement prepareStatement(String s, String[] astring) throws SQLException {
        try {
            return super.prepareStatement(s, astring);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public Clob createClob() throws SQLException {
        try {
            return super.delegate.createClob();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public Blob createBlob() throws SQLException {
        try {
            return super.delegate.createBlob();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public NClob createNClob() throws SQLException {
        try {
            return super.delegate.createNClob();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public SQLXML createSQLXML() throws SQLException {
        try {
            return super.delegate.createSQLXML();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public boolean isValid(int i) throws SQLException {
        try {
            return super.delegate.isValid(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void setClientInfo(String s, String s1) throws SQLClientInfoException {
        ((Connection) super.delegate).setClientInfo(s, s1);
    }

    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        ((Connection) super.delegate).setClientInfo(properties);
    }

    public String getClientInfo(String s) throws SQLException {
        try {
            return super.delegate.getClientInfo(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public Properties getClientInfo() throws SQLException {
        try {
            return super.delegate.getClientInfo();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public Array createArrayOf(String s, Object[] aobject) throws SQLException {
        try {
            return super.delegate.createArrayOf(s, aobject);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public Struct createStruct(String s, Object[] aobject) throws SQLException {
        try {
            return super.delegate.createStruct(s, aobject);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void setSchema(String s) throws SQLException {
        try {
            super.setSchema(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public String getSchema() throws SQLException {
        try {
            return super.delegate.getSchema();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void abort(Executor executor) throws SQLException {
        try {
            super.delegate.abort(executor);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void setNetworkTimeout(Executor executor, int i) throws SQLException {
        try {
            super.setNetworkTimeout(executor, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public int getNetworkTimeout() throws SQLException {
        try {
            return super.delegate.getNetworkTimeout();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void beginRequest() throws SQLException {
        try {
            super.delegate.beginRequest();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void endRequest() throws SQLException {
        try {
            super.delegate.endRequest();
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public boolean setShardingKeyIfValid(ShardingKey shardingkey, ShardingKey shardingkey1, int i) throws SQLException {
        try {
            return super.delegate.setShardingKeyIfValid(shardingkey, shardingkey1, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public boolean setShardingKeyIfValid(ShardingKey shardingkey, int i) throws SQLException {
        try {
            return super.delegate.setShardingKeyIfValid(shardingkey, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void setShardingKey(ShardingKey shardingkey, ShardingKey shardingkey1) throws SQLException {
        try {
            super.delegate.setShardingKey(shardingkey, shardingkey1);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    public void setShardingKey(ShardingKey shardingkey) throws SQLException {
        try {
            super.delegate.setShardingKey(shardingkey);
        } catch (SQLException sqlexception) {
            throw ((ProxyConnection) this).checkException(sqlexception);
        }
    }

    protected HikariProxyConnection(PoolEntry poolentry, Connection connection, FastList fastlist, ProxyLeakTask proxyleaktask, boolean flag, boolean flag1) {
        super(poolentry, connection, fastlist, proxyleaktask, flag, flag1);
    }
}
