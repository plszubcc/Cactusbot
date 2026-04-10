package com.zaxxer.hikari.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.util.Calendar;
import java.util.Map;

public final class HikariProxyCallableStatement extends ProxyCallableStatement implements Wrapper, AutoCloseable, Statement, PreparedStatement, CallableStatement {

    public boolean isWrapperFor(Class oclass) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).isWrapperFor(oclass);
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
            return ((CallableStatement) super.delegate).getMaxFieldSize();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setMaxFieldSize(int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setMaxFieldSize(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getMaxRows() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getMaxRows();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setMaxRows(int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setMaxRows(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setEscapeProcessing(boolean flag) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setEscapeProcessing(flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getQueryTimeout() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getQueryTimeout();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setQueryTimeout(int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setQueryTimeout(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void cancel() throws SQLException {
        try {
            ((CallableStatement) super.delegate).cancel();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getWarnings();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void clearWarnings() throws SQLException {
        try {
            ((CallableStatement) super.delegate).clearWarnings();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setCursorName(String s) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setCursorName(s);
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
            return ((CallableStatement) super.delegate).getUpdateCount();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean getMoreResults() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getMoreResults();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setFetchDirection(int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setFetchDirection(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getFetchDirection() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getFetchDirection();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setFetchSize(int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setFetchSize(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getFetchSize() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getFetchSize();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getResultSetConcurrency() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getResultSetConcurrency();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getResultSetType() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getResultSetType();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void addBatch(String s) throws SQLException {
        try {
            ((CallableStatement) super.delegate).addBatch(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void clearBatch() throws SQLException {
        try {
            ((CallableStatement) super.delegate).clearBatch();
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
            return ((CallableStatement) super.delegate).getMoreResults(i);
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
            return ((CallableStatement) super.delegate).getResultSetHoldability();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean isClosed() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).isClosed();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setPoolable(boolean flag) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setPoolable(flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean isPoolable() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).isPoolable();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void closeOnCompletion() throws SQLException {
        try {
            ((CallableStatement) super.delegate).closeOnCompletion();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean isCloseOnCompletion() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).isCloseOnCompletion();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long getLargeUpdateCount() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getLargeUpdateCount();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setLargeMaxRows(long i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setLargeMaxRows(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long getLargeMaxRows() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getLargeMaxRows();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long[] executeLargeBatch() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).executeLargeBatch();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).executeLargeUpdate(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate(String s, int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).executeLargeUpdate(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate(String s, int[] aint) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).executeLargeUpdate(s, aint);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate(String s, String[] astring) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).executeLargeUpdate(s, astring);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String enquoteLiteral(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).enquoteLiteral(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String enquoteIdentifier(String s, boolean flag) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).enquoteIdentifier(s, flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean isSimpleIdentifier(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).isSimpleIdentifier(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String enquoteNCharLiteral(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).enquoteNCharLiteral(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public ResultSet executeQuery() throws SQLException {
        try {
            return super.executeQuery();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int executeUpdate() throws SQLException {
        try {
            return super.executeUpdate();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNull(int i, int j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNull(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBoolean(int i, boolean flag) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBoolean(i, flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setByte(int i, byte b0) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setByte(i, b0);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setShort(int i, short short0) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setShort(i, short0);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setInt(int i, int j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setInt(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setLong(int i, long j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setLong(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setFloat(int i, float f) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setFloat(i, f);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setDouble(int i, double d0) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setDouble(i, d0);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBigDecimal(int i, BigDecimal bigdecimal) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBigDecimal(i, bigdecimal);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setString(int i, String s) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setString(i, s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBytes(int i, byte[] abyte) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBytes(i, abyte);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setDate(int i, Date date) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setDate(i, date);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setTime(int i, Time time) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setTime(i, time);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setTimestamp(i, timestamp);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setAsciiStream(int i, InputStream inputstream, int j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setAsciiStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setUnicodeStream(int i, InputStream inputstream, int j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setUnicodeStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBinaryStream(int i, InputStream inputstream, int j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBinaryStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void clearParameters() throws SQLException {
        try {
            ((CallableStatement) super.delegate).clearParameters();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(int i, Object object, int j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setObject(i, object, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(int i, Object object) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setObject(i, object);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean execute() throws SQLException {
        try {
            return super.execute();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void addBatch() throws SQLException {
        try {
            ((CallableStatement) super.delegate).addBatch();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setCharacterStream(int i, Reader reader, int j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setCharacterStream(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setRef(int i, Ref ref) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setRef(i, ref);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBlob(int i, Blob blob) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBlob(i, blob);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setClob(int i, Clob clob) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setClob(i, clob);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setArray(int i, Array array) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setArray(i, array);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getMetaData();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setDate(int i, Date date, Calendar calendar) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setDate(i, date, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setTime(i, time, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setTimestamp(i, timestamp, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNull(int i, int j, String s) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNull(i, j, s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setURL(int i, URL url) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setURL(i, url);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getParameterMetaData();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setRowId(int i, RowId rowid) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setRowId(i, rowid);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNString(int i, String s) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNString(i, s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNCharacterStream(int i, Reader reader, long j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNCharacterStream(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNClob(int i, NClob nclob) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNClob(i, nclob);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setClob(int i, Reader reader, long j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setClob(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBlob(int i, InputStream inputstream, long j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBlob(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNClob(int i, Reader reader, long j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNClob(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setSQLXML(int i, SQLXML sqlxml) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setSQLXML(i, sqlxml);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(int i, Object object, int j, int k) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setObject(i, object, j, k);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setAsciiStream(int i, InputStream inputstream, long j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setAsciiStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBinaryStream(int i, InputStream inputstream, long j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBinaryStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setCharacterStream(int i, Reader reader, long j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setCharacterStream(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setAsciiStream(int i, InputStream inputstream) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setAsciiStream(i, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBinaryStream(int i, InputStream inputstream) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBinaryStream(i, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setCharacterStream(int i, Reader reader) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setCharacterStream(i, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNCharacterStream(int i, Reader reader) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNCharacterStream(i, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setClob(int i, Reader reader) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setClob(i, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBlob(int i, InputStream inputstream) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBlob(i, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNClob(int i, Reader reader) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNClob(i, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(int i, Object object, SQLType sqltype, int j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setObject(i, object, sqltype, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(int i, Object object, SQLType sqltype) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setObject(i, object, sqltype);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).executeLargeUpdate();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void registerOutParameter(int i, int j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).registerOutParameter(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void registerOutParameter(int i, int j, int k) throws SQLException {
        try {
            ((CallableStatement) super.delegate).registerOutParameter(i, j, k);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean wasNull() throws SQLException {
        try {
            return ((CallableStatement) super.delegate).wasNull();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String getString(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getString(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean getBoolean(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getBoolean(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public byte getByte(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getByte(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public short getShort(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getShort(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getInt(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getInt(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long getLong(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getLong(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public float getFloat(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getFloat(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public double getDouble(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getDouble(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public BigDecimal getBigDecimal(int i, int j) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getBigDecimal(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public byte[] getBytes(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getBytes(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Date getDate(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getDate(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Time getTime(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getTime(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Timestamp getTimestamp(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getTimestamp(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Object getObject(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getObject(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public BigDecimal getBigDecimal(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getBigDecimal(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Object getObject(int i, Map map) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getObject(i, map);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Ref getRef(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getRef(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Blob getBlob(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getBlob(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Clob getClob(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getClob(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Array getArray(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getArray(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Date getDate(int i, Calendar calendar) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getDate(i, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Time getTime(int i, Calendar calendar) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getTime(i, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getTimestamp(i, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void registerOutParameter(int i, int j, String s) throws SQLException {
        try {
            ((CallableStatement) super.delegate).registerOutParameter(i, j, s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void registerOutParameter(String s, int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).registerOutParameter(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void registerOutParameter(String s, int i, int j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).registerOutParameter(s, i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void registerOutParameter(String s, int i, String s1) throws SQLException {
        try {
            ((CallableStatement) super.delegate).registerOutParameter(s, i, s1);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public URL getURL(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getURL(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setURL(String s, URL url) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setURL(s, url);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNull(String s, int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNull(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBoolean(String s, boolean flag) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBoolean(s, flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setByte(String s, byte b0) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setByte(s, b0);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setShort(String s, short short0) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setShort(s, short0);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setInt(String s, int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setInt(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setLong(String s, long i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setLong(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setFloat(String s, float f) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setFloat(s, f);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setDouble(String s, double d0) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setDouble(s, d0);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBigDecimal(String s, BigDecimal bigdecimal) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBigDecimal(s, bigdecimal);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setString(String s, String s1) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setString(s, s1);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBytes(String s, byte[] abyte) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBytes(s, abyte);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setDate(String s, Date date) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setDate(s, date);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setTime(String s, Time time) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setTime(s, time);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setTimestamp(String s, Timestamp timestamp) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setTimestamp(s, timestamp);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setAsciiStream(String s, InputStream inputstream, int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setAsciiStream(s, inputstream, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBinaryStream(String s, InputStream inputstream, int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBinaryStream(s, inputstream, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(String s, Object object, int i, int j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setObject(s, object, i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(String s, Object object, int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setObject(s, object, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(String s, Object object) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setObject(s, object);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setCharacterStream(String s, Reader reader, int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setCharacterStream(s, reader, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setDate(String s, Date date, Calendar calendar) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setDate(s, date, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setTime(String s, Time time, Calendar calendar) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setTime(s, time, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setTimestamp(String s, Timestamp timestamp, Calendar calendar) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setTimestamp(s, timestamp, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNull(String s, int i, String s1) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNull(s, i, s1);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String getString(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getString(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean getBoolean(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getBoolean(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public byte getByte(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getByte(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public short getShort(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getShort(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getInt(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getInt(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long getLong(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getLong(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public float getFloat(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getFloat(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public double getDouble(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getDouble(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public byte[] getBytes(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getBytes(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Date getDate(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getDate(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Time getTime(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getTime(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Timestamp getTimestamp(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getTimestamp(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Object getObject(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getObject(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public BigDecimal getBigDecimal(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getBigDecimal(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Object getObject(String s, Map map) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getObject(s, map);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Ref getRef(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getRef(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Blob getBlob(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getBlob(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Clob getClob(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getClob(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Array getArray(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getArray(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Date getDate(String s, Calendar calendar) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getDate(s, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Time getTime(String s, Calendar calendar) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getTime(s, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getTimestamp(s, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public URL getURL(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getURL(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public RowId getRowId(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getRowId(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public RowId getRowId(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getRowId(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setRowId(String s, RowId rowid) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setRowId(s, rowid);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNString(String s, String s1) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNString(s, s1);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNCharacterStream(String s, Reader reader, long i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNCharacterStream(s, reader, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNClob(String s, NClob nclob) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNClob(s, nclob);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setClob(String s, Reader reader, long i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setClob(s, reader, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBlob(String s, InputStream inputstream, long i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBlob(s, inputstream, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNClob(String s, Reader reader, long i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNClob(s, reader, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public NClob getNClob(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getNClob(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public NClob getNClob(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getNClob(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setSQLXML(String s, SQLXML sqlxml) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setSQLXML(s, sqlxml);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public SQLXML getSQLXML(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getSQLXML(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public SQLXML getSQLXML(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getSQLXML(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String getNString(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getNString(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String getNString(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getNString(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Reader getNCharacterStream(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getNCharacterStream(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Reader getNCharacterStream(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getNCharacterStream(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Reader getCharacterStream(int i) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getCharacterStream(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Reader getCharacterStream(String s) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getCharacterStream(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBlob(String s, Blob blob) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBlob(s, blob);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setClob(String s, Clob clob) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setClob(s, clob);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setAsciiStream(String s, InputStream inputstream, long i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setAsciiStream(s, inputstream, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBinaryStream(String s, InputStream inputstream, long i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBinaryStream(s, inputstream, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setCharacterStream(String s, Reader reader, long i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setCharacterStream(s, reader, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setAsciiStream(String s, InputStream inputstream) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setAsciiStream(s, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBinaryStream(String s, InputStream inputstream) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBinaryStream(s, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setCharacterStream(String s, Reader reader) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setCharacterStream(s, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNCharacterStream(String s, Reader reader) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNCharacterStream(s, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setClob(String s, Reader reader) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setClob(s, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBlob(String s, InputStream inputstream) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setBlob(s, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNClob(String s, Reader reader) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setNClob(s, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Object getObject(int i, Class oclass) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getObject(i, oclass);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public Object getObject(String s, Class oclass) throws SQLException {
        try {
            return ((CallableStatement) super.delegate).getObject(s, oclass);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(String s, Object object, SQLType sqltype, int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setObject(s, object, sqltype, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(String s, Object object, SQLType sqltype) throws SQLException {
        try {
            ((CallableStatement) super.delegate).setObject(s, object, sqltype);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void registerOutParameter(int i, SQLType sqltype) throws SQLException {
        try {
            ((CallableStatement) super.delegate).registerOutParameter(i, sqltype);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void registerOutParameter(int i, SQLType sqltype, int j) throws SQLException {
        try {
            ((CallableStatement) super.delegate).registerOutParameter(i, sqltype, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void registerOutParameter(int i, SQLType sqltype, String s) throws SQLException {
        try {
            ((CallableStatement) super.delegate).registerOutParameter(i, sqltype, s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void registerOutParameter(String s, SQLType sqltype) throws SQLException {
        try {
            ((CallableStatement) super.delegate).registerOutParameter(s, sqltype);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void registerOutParameter(String s, SQLType sqltype, int i) throws SQLException {
        try {
            ((CallableStatement) super.delegate).registerOutParameter(s, sqltype, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void registerOutParameter(String s, SQLType sqltype, String s1) throws SQLException {
        try {
            ((CallableStatement) super.delegate).registerOutParameter(s, sqltype, s1);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    protected HikariProxyCallableStatement(ProxyConnection proxyconnection, CallableStatement callablestatement) {
        super(proxyconnection, callablestatement);
    }
}
