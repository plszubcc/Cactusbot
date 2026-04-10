package com.zaxxer.hikari.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
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

public final class HikariProxyPreparedStatement extends ProxyPreparedStatement implements Wrapper, AutoCloseable, Statement, PreparedStatement {

    public boolean isWrapperFor(Class oclass) throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).isWrapperFor(oclass);
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
            return ((PreparedStatement) super.delegate).getMaxFieldSize();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setMaxFieldSize(int i) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setMaxFieldSize(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getMaxRows() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).getMaxRows();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setMaxRows(int i) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setMaxRows(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setEscapeProcessing(boolean flag) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setEscapeProcessing(flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getQueryTimeout() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).getQueryTimeout();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setQueryTimeout(int i) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setQueryTimeout(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void cancel() throws SQLException {
        try {
            ((PreparedStatement) super.delegate).cancel();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).getWarnings();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void clearWarnings() throws SQLException {
        try {
            ((PreparedStatement) super.delegate).clearWarnings();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setCursorName(String s) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setCursorName(s);
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
            return ((PreparedStatement) super.delegate).getUpdateCount();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean getMoreResults() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).getMoreResults();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setFetchDirection(int i) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setFetchDirection(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getFetchDirection() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).getFetchDirection();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setFetchSize(int i) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setFetchSize(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getFetchSize() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).getFetchSize();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getResultSetConcurrency() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).getResultSetConcurrency();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public int getResultSetType() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).getResultSetType();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void addBatch(String s) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).addBatch(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void clearBatch() throws SQLException {
        try {
            ((PreparedStatement) super.delegate).clearBatch();
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
            return ((PreparedStatement) super.delegate).getMoreResults(i);
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
            return ((PreparedStatement) super.delegate).getResultSetHoldability();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean isClosed() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).isClosed();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setPoolable(boolean flag) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setPoolable(flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean isPoolable() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).isPoolable();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void closeOnCompletion() throws SQLException {
        try {
            ((PreparedStatement) super.delegate).closeOnCompletion();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean isCloseOnCompletion() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).isCloseOnCompletion();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long getLargeUpdateCount() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).getLargeUpdateCount();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setLargeMaxRows(long i) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setLargeMaxRows(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long getLargeMaxRows() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).getLargeMaxRows();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long[] executeLargeBatch() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).executeLargeBatch();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate(String s) throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).executeLargeUpdate(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate(String s, int i) throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).executeLargeUpdate(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate(String s, int[] aint) throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).executeLargeUpdate(s, aint);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate(String s, String[] astring) throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).executeLargeUpdate(s, astring);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String enquoteLiteral(String s) throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).enquoteLiteral(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String enquoteIdentifier(String s, boolean flag) throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).enquoteIdentifier(s, flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public boolean isSimpleIdentifier(String s) throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).isSimpleIdentifier(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public String enquoteNCharLiteral(String s) throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).enquoteNCharLiteral(s);
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
            ((PreparedStatement) super.delegate).setNull(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBoolean(int i, boolean flag) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setBoolean(i, flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setByte(int i, byte b0) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setByte(i, b0);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setShort(int i, short short0) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setShort(i, short0);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setInt(int i, int j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setInt(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setLong(int i, long j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setLong(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setFloat(int i, float f) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setFloat(i, f);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setDouble(int i, double d0) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setDouble(i, d0);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBigDecimal(int i, BigDecimal bigdecimal) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setBigDecimal(i, bigdecimal);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setString(int i, String s) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setString(i, s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBytes(int i, byte[] abyte) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setBytes(i, abyte);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setDate(int i, Date date) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setDate(i, date);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setTime(int i, Time time) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setTime(i, time);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setTimestamp(int i, Timestamp timestamp) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setTimestamp(i, timestamp);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setAsciiStream(int i, InputStream inputstream, int j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setAsciiStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setUnicodeStream(int i, InputStream inputstream, int j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setUnicodeStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBinaryStream(int i, InputStream inputstream, int j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setBinaryStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void clearParameters() throws SQLException {
        try {
            ((PreparedStatement) super.delegate).clearParameters();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(int i, Object object, int j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setObject(i, object, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(int i, Object object) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setObject(i, object);
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
            ((PreparedStatement) super.delegate).addBatch();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setCharacterStream(int i, Reader reader, int j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setCharacterStream(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setRef(int i, Ref ref) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setRef(i, ref);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBlob(int i, Blob blob) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setBlob(i, blob);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setClob(int i, Clob clob) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setClob(i, clob);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setArray(int i, Array array) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setArray(i, array);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).getMetaData();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setDate(int i, Date date, Calendar calendar) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setDate(i, date, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setTime(int i, Time time, Calendar calendar) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setTime(i, time, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setTimestamp(int i, Timestamp timestamp, Calendar calendar) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setTimestamp(i, timestamp, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNull(int i, int j, String s) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setNull(i, j, s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setURL(int i, URL url) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setURL(i, url);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public ParameterMetaData getParameterMetaData() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).getParameterMetaData();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setRowId(int i, RowId rowid) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setRowId(i, rowid);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNString(int i, String s) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setNString(i, s);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNCharacterStream(int i, Reader reader, long j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setNCharacterStream(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNClob(int i, NClob nclob) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setNClob(i, nclob);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setClob(int i, Reader reader, long j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setClob(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBlob(int i, InputStream inputstream, long j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setBlob(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNClob(int i, Reader reader, long j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setNClob(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setSQLXML(int i, SQLXML sqlxml) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setSQLXML(i, sqlxml);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(int i, Object object, int j, int k) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setObject(i, object, j, k);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setAsciiStream(int i, InputStream inputstream, long j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setAsciiStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBinaryStream(int i, InputStream inputstream, long j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setBinaryStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setCharacterStream(int i, Reader reader, long j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setCharacterStream(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setAsciiStream(int i, InputStream inputstream) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setAsciiStream(i, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBinaryStream(int i, InputStream inputstream) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setBinaryStream(i, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setCharacterStream(int i, Reader reader) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setCharacterStream(i, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNCharacterStream(int i, Reader reader) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setNCharacterStream(i, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setClob(int i, Reader reader) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setClob(i, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setBlob(int i, InputStream inputstream) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setBlob(i, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setNClob(int i, Reader reader) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setNClob(i, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(int i, Object object, SQLType sqltype, int j) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setObject(i, object, sqltype, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public void setObject(int i, Object object, SQLType sqltype) throws SQLException {
        try {
            ((PreparedStatement) super.delegate).setObject(i, object, sqltype);
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    public long executeLargeUpdate() throws SQLException {
        try {
            return ((PreparedStatement) super.delegate).executeLargeUpdate();
        } catch (SQLException sqlexception) {
            throw ((ProxyStatement) this).checkException(sqlexception);
        }
    }

    HikariProxyPreparedStatement(ProxyConnection proxyconnection, PreparedStatement preparedstatement) {
        super(proxyconnection, preparedstatement);
    }
}
