package com.zaxxer.hikari.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Wrapper;

public final class HikariProxyStatement extends ProxyStatement implements Wrapper, AutoCloseable, Statement {

    public boolean isWrapperFor(Class oclass) throws SQLException {
        try {
            return super.delegate.isWrapperFor(oclass);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public ResultSet executeQuery(String s) throws SQLException {
        try {
            return super.executeQuery(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int executeUpdate(String s) throws SQLException {
        try {
            return super.executeUpdate(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getMaxFieldSize() throws SQLException {
        try {
            return super.delegate.getMaxFieldSize();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setMaxFieldSize(int i) throws SQLException {
        try {
            super.delegate.setMaxFieldSize(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getMaxRows() throws SQLException {
        try {
            return super.delegate.getMaxRows();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setMaxRows(int i) throws SQLException {
        try {
            super.delegate.setMaxRows(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setEscapeProcessing(boolean flag) throws SQLException {
        try {
            super.delegate.setEscapeProcessing(flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getQueryTimeout() throws SQLException {
        try {
            return super.delegate.getQueryTimeout();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setQueryTimeout(int i) throws SQLException {
        try {
            super.delegate.setQueryTimeout(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void cancel() throws SQLException {
        try {
            super.delegate.cancel();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        try {
            return super.delegate.getWarnings();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void clearWarnings() throws SQLException {
        try {
            super.delegate.clearWarnings();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setCursorName(String s) throws SQLException {
        try {
            super.delegate.setCursorName(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean execute(String s) throws SQLException {
        try {
            return super.execute(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public ResultSet getResultSet() throws SQLException {
        try {
            return super.getResultSet();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getUpdateCount() throws SQLException {
        try {
            return super.delegate.getUpdateCount();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean getMoreResults() throws SQLException {
        try {
            return super.delegate.getMoreResults();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setFetchDirection(int i) throws SQLException {
        try {
            super.delegate.setFetchDirection(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getFetchDirection() throws SQLException {
        try {
            return super.delegate.getFetchDirection();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setFetchSize(int i) throws SQLException {
        try {
            super.delegate.setFetchSize(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getFetchSize() throws SQLException {
        try {
            return super.delegate.getFetchSize();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getResultSetConcurrency() throws SQLException {
        try {
            return super.delegate.getResultSetConcurrency();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getResultSetType() throws SQLException {
        try {
            return super.delegate.getResultSetType();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void addBatch(String s) throws SQLException {
        try {
            super.delegate.addBatch(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void clearBatch() throws SQLException {
        try {
            super.delegate.clearBatch();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int[] executeBatch() throws SQLException {
        try {
            return super.executeBatch();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Connection getConnection() throws SQLException {
        try {
            return super.getConnection();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean getMoreResults(int i) throws SQLException {
        try {
            return super.delegate.getMoreResults(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        try {
            return super.getGeneratedKeys();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int executeUpdate(String s, int i) throws SQLException {
        try {
            return super.executeUpdate(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int executeUpdate(String s, int[] aint) throws SQLException {
        try {
            return super.executeUpdate(s, aint);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int executeUpdate(String s, String[] astring) throws SQLException {
        try {
            return super.executeUpdate(s, astring);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean execute(String s, int i) throws SQLException {
        try {
            return super.execute(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean execute(String s, int[] aint) throws SQLException {
        try {
            return super.execute(s, aint);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean execute(String s, String[] astring) throws SQLException {
        try {
            return super.execute(s, astring);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getResultSetHoldability() throws SQLException {
        try {
            return super.delegate.getResultSetHoldability();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean isClosed() throws SQLException {
        try {
            return super.delegate.isClosed();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setPoolable(boolean flag) throws SQLException {
        try {
            super.delegate.setPoolable(flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean isPoolable() throws SQLException {
        try {
            return super.delegate.isPoolable();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void closeOnCompletion() throws SQLException {
        try {
            super.delegate.closeOnCompletion();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean isCloseOnCompletion() throws SQLException {
        try {
            return super.delegate.isCloseOnCompletion();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long getLargeUpdateCount() throws SQLException {
        try {
            return super.delegate.getLargeUpdateCount();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setLargeMaxRows(long i) throws SQLException {
        try {
            super.delegate.setLargeMaxRows(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long getLargeMaxRows() throws SQLException {
        try {
            return super.delegate.getLargeMaxRows();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long[] executeLargeBatch() throws SQLException {
        try {
            return super.delegate.executeLargeBatch();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate(String s) throws SQLException {
        try {
            return super.delegate.executeLargeUpdate(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate(String s, int i) throws SQLException {
        try {
            return super.delegate.executeLargeUpdate(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate(String s, int[] aint) throws SQLException {
        try {
            return super.delegate.executeLargeUpdate(s, aint);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate(String s, String[] astring) throws SQLException {
        try {
            return super.delegate.executeLargeUpdate(s, astring);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String enquoteLiteral(String s) throws SQLException {
        try {
            return super.delegate.enquoteLiteral(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String enquoteIdentifier(String s, boolean flag) throws SQLException {
        try {
            return super.delegate.enquoteIdentifier(s, flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean isSimpleIdentifier(String s) throws SQLException {
        try {
            return super.delegate.isSimpleIdentifier(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String enquoteNCharLiteral(String s) throws SQLException {
        try {
            return super.delegate.enquoteNCharLiteral(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    HikariProxyStatement(ProxyConnection proxyconnection, Statement statement) {
        super(proxyconnection, statement);
    }
}
