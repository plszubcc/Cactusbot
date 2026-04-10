package com.zaxxer.hikari.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.util.Calendar;
import java.util.Map;

public final class HikariProxyResultSet extends ProxyResultSet implements Wrapper, AutoCloseable, ResultSet {

    public boolean isWrapperFor(Class oclass) throws SQLException {
        try {
            return super.delegate.isWrapperFor(oclass);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void close() throws Exception {
        ((ResultSet) super.delegate).close();
    }

    public boolean next() throws SQLException {
        try {
            return super.delegate.next();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean wasNull() throws SQLException {
        try {
            return super.delegate.wasNull();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public String getString(int i) throws SQLException {
        try {
            return super.delegate.getString(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean getBoolean(int i) throws SQLException {
        try {
            return super.delegate.getBoolean(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public byte getByte(int i) throws SQLException {
        try {
            return super.delegate.getByte(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public short getShort(int i) throws SQLException {
        try {
            return super.delegate.getShort(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public int getInt(int i) throws SQLException {
        try {
            return super.delegate.getInt(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public long getLong(int i) throws SQLException {
        try {
            return super.delegate.getLong(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public float getFloat(int i) throws SQLException {
        try {
            return super.delegate.getFloat(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public double getDouble(int i) throws SQLException {
        try {
            return super.delegate.getDouble(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public BigDecimal getBigDecimal(int i, int j) throws SQLException {
        try {
            return super.delegate.getBigDecimal(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public byte[] getBytes(int i) throws SQLException {
        try {
            return super.delegate.getBytes(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Date getDate(int i) throws SQLException {
        try {
            return super.delegate.getDate(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Time getTime(int i) throws SQLException {
        try {
            return super.delegate.getTime(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Timestamp getTimestamp(int i) throws SQLException {
        try {
            return super.delegate.getTimestamp(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public InputStream getAsciiStream(int i) throws SQLException {
        try {
            return super.delegate.getAsciiStream(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public InputStream getUnicodeStream(int i) throws SQLException {
        try {
            return super.delegate.getUnicodeStream(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public InputStream getBinaryStream(int i) throws SQLException {
        try {
            return super.delegate.getBinaryStream(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public String getString(String s) throws SQLException {
        try {
            return super.delegate.getString(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean getBoolean(String s) throws SQLException {
        try {
            return super.delegate.getBoolean(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public byte getByte(String s) throws SQLException {
        try {
            return super.delegate.getByte(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public short getShort(String s) throws SQLException {
        try {
            return super.delegate.getShort(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public int getInt(String s) throws SQLException {
        try {
            return super.delegate.getInt(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public long getLong(String s) throws SQLException {
        try {
            return super.delegate.getLong(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public float getFloat(String s) throws SQLException {
        try {
            return super.delegate.getFloat(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public double getDouble(String s) throws SQLException {
        try {
            return super.delegate.getDouble(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public BigDecimal getBigDecimal(String s, int i) throws SQLException {
        try {
            return super.delegate.getBigDecimal(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public byte[] getBytes(String s) throws SQLException {
        try {
            return super.delegate.getBytes(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Date getDate(String s) throws SQLException {
        try {
            return super.delegate.getDate(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Time getTime(String s) throws SQLException {
        try {
            return super.delegate.getTime(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Timestamp getTimestamp(String s) throws SQLException {
        try {
            return super.delegate.getTimestamp(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public InputStream getAsciiStream(String s) throws SQLException {
        try {
            return super.delegate.getAsciiStream(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public InputStream getUnicodeStream(String s) throws SQLException {
        try {
            return super.delegate.getUnicodeStream(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public InputStream getBinaryStream(String s) throws SQLException {
        try {
            return super.delegate.getBinaryStream(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public SQLWarning getWarnings() throws SQLException {
        try {
            return super.delegate.getWarnings();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void clearWarnings() throws SQLException {
        try {
            super.delegate.clearWarnings();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public String getCursorName() throws SQLException {
        try {
            return super.delegate.getCursorName();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return super.delegate.getMetaData();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Object getObject(int i) throws SQLException {
        try {
            return super.delegate.getObject(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Object getObject(String s) throws SQLException {
        try {
            return super.delegate.getObject(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public int findColumn(String s) throws SQLException {
        try {
            return super.delegate.findColumn(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Reader getCharacterStream(int i) throws SQLException {
        try {
            return super.delegate.getCharacterStream(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Reader getCharacterStream(String s) throws SQLException {
        try {
            return super.delegate.getCharacterStream(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public BigDecimal getBigDecimal(int i) throws SQLException {
        try {
            return super.delegate.getBigDecimal(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public BigDecimal getBigDecimal(String s) throws SQLException {
        try {
            return super.delegate.getBigDecimal(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean isBeforeFirst() throws SQLException {
        try {
            return super.delegate.isBeforeFirst();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean isAfterLast() throws SQLException {
        try {
            return super.delegate.isAfterLast();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean isFirst() throws SQLException {
        try {
            return super.delegate.isFirst();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean isLast() throws SQLException {
        try {
            return super.delegate.isLast();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void beforeFirst() throws SQLException {
        try {
            super.delegate.beforeFirst();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void afterLast() throws SQLException {
        try {
            super.delegate.afterLast();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean first() throws SQLException {
        try {
            return super.delegate.first();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean last() throws SQLException {
        try {
            return super.delegate.last();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public int getRow() throws SQLException {
        try {
            return super.delegate.getRow();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean absolute(int i) throws SQLException {
        try {
            return super.delegate.absolute(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean relative(int i) throws SQLException {
        try {
            return super.delegate.relative(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean previous() throws SQLException {
        try {
            return super.delegate.previous();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void setFetchDirection(int i) throws SQLException {
        try {
            super.delegate.setFetchDirection(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public int getFetchDirection() throws SQLException {
        try {
            return super.delegate.getFetchDirection();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void setFetchSize(int i) throws SQLException {
        try {
            super.delegate.setFetchSize(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public int getFetchSize() throws SQLException {
        try {
            return super.delegate.getFetchSize();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public int getType() throws SQLException {
        try {
            return super.delegate.getType();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public int getConcurrency() throws SQLException {
        try {
            return super.delegate.getConcurrency();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean rowUpdated() throws SQLException {
        try {
            return super.delegate.rowUpdated();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean rowInserted() throws SQLException {
        try {
            return super.delegate.rowInserted();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean rowDeleted() throws SQLException {
        try {
            return super.delegate.rowDeleted();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNull(int i) throws SQLException {
        try {
            super.delegate.updateNull(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBoolean(int i, boolean flag) throws SQLException {
        try {
            super.delegate.updateBoolean(i, flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateByte(int i, byte b0) throws SQLException {
        try {
            super.delegate.updateByte(i, b0);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateShort(int i, short short0) throws SQLException {
        try {
            super.delegate.updateShort(i, short0);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateInt(int i, int j) throws SQLException {
        try {
            super.delegate.updateInt(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateLong(int i, long j) throws SQLException {
        try {
            super.delegate.updateLong(i, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateFloat(int i, float f) throws SQLException {
        try {
            super.delegate.updateFloat(i, f);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateDouble(int i, double d0) throws SQLException {
        try {
            super.delegate.updateDouble(i, d0);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBigDecimal(int i, BigDecimal bigdecimal) throws SQLException {
        try {
            super.delegate.updateBigDecimal(i, bigdecimal);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateString(int i, String s) throws SQLException {
        try {
            super.delegate.updateString(i, s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBytes(int i, byte[] abyte) throws SQLException {
        try {
            super.delegate.updateBytes(i, abyte);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateDate(int i, Date date) throws SQLException {
        try {
            super.delegate.updateDate(i, date);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateTime(int i, Time time) throws SQLException {
        try {
            super.delegate.updateTime(i, time);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateTimestamp(int i, Timestamp timestamp) throws SQLException {
        try {
            super.delegate.updateTimestamp(i, timestamp);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateAsciiStream(int i, InputStream inputstream, int j) throws SQLException {
        try {
            super.delegate.updateAsciiStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBinaryStream(int i, InputStream inputstream, int j) throws SQLException {
        try {
            super.delegate.updateBinaryStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateCharacterStream(int i, Reader reader, int j) throws SQLException {
        try {
            super.delegate.updateCharacterStream(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateObject(int i, Object object, int j) throws SQLException {
        try {
            super.delegate.updateObject(i, object, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateObject(int i, Object object) throws SQLException {
        try {
            super.delegate.updateObject(i, object);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNull(String s) throws SQLException {
        try {
            super.delegate.updateNull(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBoolean(String s, boolean flag) throws SQLException {
        try {
            super.delegate.updateBoolean(s, flag);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateByte(String s, byte b0) throws SQLException {
        try {
            super.delegate.updateByte(s, b0);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateShort(String s, short short0) throws SQLException {
        try {
            super.delegate.updateShort(s, short0);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateInt(String s, int i) throws SQLException {
        try {
            super.delegate.updateInt(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateLong(String s, long i) throws SQLException {
        try {
            super.delegate.updateLong(s, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateFloat(String s, float f) throws SQLException {
        try {
            super.delegate.updateFloat(s, f);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateDouble(String s, double d0) throws SQLException {
        try {
            super.delegate.updateDouble(s, d0);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBigDecimal(String s, BigDecimal bigdecimal) throws SQLException {
        try {
            super.delegate.updateBigDecimal(s, bigdecimal);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateString(String s, String s1) throws SQLException {
        try {
            super.delegate.updateString(s, s1);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBytes(String s, byte[] abyte) throws SQLException {
        try {
            super.delegate.updateBytes(s, abyte);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateDate(String s, Date date) throws SQLException {
        try {
            super.delegate.updateDate(s, date);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateTime(String s, Time time) throws SQLException {
        try {
            super.delegate.updateTime(s, time);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateTimestamp(String s, Timestamp timestamp) throws SQLException {
        try {
            super.delegate.updateTimestamp(s, timestamp);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateAsciiStream(String s, InputStream inputstream, int i) throws SQLException {
        try {
            super.delegate.updateAsciiStream(s, inputstream, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBinaryStream(String s, InputStream inputstream, int i) throws SQLException {
        try {
            super.delegate.updateBinaryStream(s, inputstream, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateCharacterStream(String s, Reader reader, int i) throws SQLException {
        try {
            super.delegate.updateCharacterStream(s, reader, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateObject(String s, Object object, int i) throws SQLException {
        try {
            super.delegate.updateObject(s, object, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateObject(String s, Object object) throws SQLException {
        try {
            super.delegate.updateObject(s, object);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void insertRow() throws SQLException {
        try {
            super.insertRow();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateRow() throws SQLException {
        try {
            super.updateRow();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void deleteRow() throws SQLException {
        try {
            super.deleteRow();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void refreshRow() throws SQLException {
        try {
            super.delegate.refreshRow();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void cancelRowUpdates() throws SQLException {
        try {
            super.delegate.cancelRowUpdates();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void moveToInsertRow() throws SQLException {
        try {
            super.delegate.moveToInsertRow();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void moveToCurrentRow() throws SQLException {
        try {
            super.delegate.moveToCurrentRow();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Object getObject(int i, Map map) throws SQLException {
        try {
            return super.delegate.getObject(i, map);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Ref getRef(int i) throws SQLException {
        try {
            return super.delegate.getRef(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Blob getBlob(int i) throws SQLException {
        try {
            return super.delegate.getBlob(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Clob getClob(int i) throws SQLException {
        try {
            return super.delegate.getClob(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Array getArray(int i) throws SQLException {
        try {
            return super.delegate.getArray(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Object getObject(String s, Map map) throws SQLException {
        try {
            return super.delegate.getObject(s, map);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Ref getRef(String s) throws SQLException {
        try {
            return super.delegate.getRef(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Blob getBlob(String s) throws SQLException {
        try {
            return super.delegate.getBlob(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Clob getClob(String s) throws SQLException {
        try {
            return super.delegate.getClob(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Array getArray(String s) throws SQLException {
        try {
            return super.delegate.getArray(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Date getDate(int i, Calendar calendar) throws SQLException {
        try {
            return super.delegate.getDate(i, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Date getDate(String s, Calendar calendar) throws SQLException {
        try {
            return super.delegate.getDate(s, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Time getTime(int i, Calendar calendar) throws SQLException {
        try {
            return super.delegate.getTime(i, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Time getTime(String s, Calendar calendar) throws SQLException {
        try {
            return super.delegate.getTime(s, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Timestamp getTimestamp(int i, Calendar calendar) throws SQLException {
        try {
            return super.delegate.getTimestamp(i, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Timestamp getTimestamp(String s, Calendar calendar) throws SQLException {
        try {
            return super.delegate.getTimestamp(s, calendar);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public URL getURL(int i) throws SQLException {
        try {
            return super.delegate.getURL(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public URL getURL(String s) throws SQLException {
        try {
            return super.delegate.getURL(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateRef(int i, Ref ref) throws SQLException {
        try {
            super.delegate.updateRef(i, ref);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateRef(String s, Ref ref) throws SQLException {
        try {
            super.delegate.updateRef(s, ref);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBlob(int i, Blob blob) throws SQLException {
        try {
            super.delegate.updateBlob(i, blob);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBlob(String s, Blob blob) throws SQLException {
        try {
            super.delegate.updateBlob(s, blob);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateClob(int i, Clob clob) throws SQLException {
        try {
            super.delegate.updateClob(i, clob);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateClob(String s, Clob clob) throws SQLException {
        try {
            super.delegate.updateClob(s, clob);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateArray(int i, Array array) throws SQLException {
        try {
            super.delegate.updateArray(i, array);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateArray(String s, Array array) throws SQLException {
        try {
            super.delegate.updateArray(s, array);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public RowId getRowId(int i) throws SQLException {
        try {
            return super.delegate.getRowId(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public RowId getRowId(String s) throws SQLException {
        try {
            return super.delegate.getRowId(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateRowId(int i, RowId rowid) throws SQLException {
        try {
            super.delegate.updateRowId(i, rowid);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateRowId(String s, RowId rowid) throws SQLException {
        try {
            super.delegate.updateRowId(s, rowid);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public int getHoldability() throws SQLException {
        try {
            return super.delegate.getHoldability();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public boolean isClosed() throws SQLException {
        try {
            return super.delegate.isClosed();
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNString(int i, String s) throws SQLException {
        try {
            super.delegate.updateNString(i, s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNString(String s, String s1) throws SQLException {
        try {
            super.delegate.updateNString(s, s1);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNClob(int i, NClob nclob) throws SQLException {
        try {
            super.delegate.updateNClob(i, nclob);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNClob(String s, NClob nclob) throws SQLException {
        try {
            super.delegate.updateNClob(s, nclob);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public NClob getNClob(int i) throws SQLException {
        try {
            return super.delegate.getNClob(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public NClob getNClob(String s) throws SQLException {
        try {
            return super.delegate.getNClob(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public SQLXML getSQLXML(int i) throws SQLException {
        try {
            return super.delegate.getSQLXML(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public SQLXML getSQLXML(String s) throws SQLException {
        try {
            return super.delegate.getSQLXML(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateSQLXML(int i, SQLXML sqlxml) throws SQLException {
        try {
            super.delegate.updateSQLXML(i, sqlxml);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateSQLXML(String s, SQLXML sqlxml) throws SQLException {
        try {
            super.delegate.updateSQLXML(s, sqlxml);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public String getNString(int i) throws SQLException {
        try {
            return super.delegate.getNString(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public String getNString(String s) throws SQLException {
        try {
            return super.delegate.getNString(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Reader getNCharacterStream(int i) throws SQLException {
        try {
            return super.delegate.getNCharacterStream(i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Reader getNCharacterStream(String s) throws SQLException {
        try {
            return super.delegate.getNCharacterStream(s);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNCharacterStream(int i, Reader reader, long j) throws SQLException {
        try {
            super.delegate.updateNCharacterStream(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNCharacterStream(String s, Reader reader, long i) throws SQLException {
        try {
            super.delegate.updateNCharacterStream(s, reader, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateAsciiStream(int i, InputStream inputstream, long j) throws SQLException {
        try {
            super.delegate.updateAsciiStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBinaryStream(int i, InputStream inputstream, long j) throws SQLException {
        try {
            super.delegate.updateBinaryStream(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateCharacterStream(int i, Reader reader, long j) throws SQLException {
        try {
            super.delegate.updateCharacterStream(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateAsciiStream(String s, InputStream inputstream, long i) throws SQLException {
        try {
            super.delegate.updateAsciiStream(s, inputstream, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBinaryStream(String s, InputStream inputstream, long i) throws SQLException {
        try {
            super.delegate.updateBinaryStream(s, inputstream, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateCharacterStream(String s, Reader reader, long i) throws SQLException {
        try {
            super.delegate.updateCharacterStream(s, reader, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBlob(int i, InputStream inputstream, long j) throws SQLException {
        try {
            super.delegate.updateBlob(i, inputstream, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBlob(String s, InputStream inputstream, long i) throws SQLException {
        try {
            super.delegate.updateBlob(s, inputstream, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateClob(int i, Reader reader, long j) throws SQLException {
        try {
            super.delegate.updateClob(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateClob(String s, Reader reader, long i) throws SQLException {
        try {
            super.delegate.updateClob(s, reader, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNClob(int i, Reader reader, long j) throws SQLException {
        try {
            super.delegate.updateNClob(i, reader, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNClob(String s, Reader reader, long i) throws SQLException {
        try {
            super.delegate.updateNClob(s, reader, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNCharacterStream(int i, Reader reader) throws SQLException {
        try {
            super.delegate.updateNCharacterStream(i, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNCharacterStream(String s, Reader reader) throws SQLException {
        try {
            super.delegate.updateNCharacterStream(s, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateAsciiStream(int i, InputStream inputstream) throws SQLException {
        try {
            super.delegate.updateAsciiStream(i, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBinaryStream(int i, InputStream inputstream) throws SQLException {
        try {
            super.delegate.updateBinaryStream(i, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateCharacterStream(int i, Reader reader) throws SQLException {
        try {
            super.delegate.updateCharacterStream(i, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateAsciiStream(String s, InputStream inputstream) throws SQLException {
        try {
            super.delegate.updateAsciiStream(s, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBinaryStream(String s, InputStream inputstream) throws SQLException {
        try {
            super.delegate.updateBinaryStream(s, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateCharacterStream(String s, Reader reader) throws SQLException {
        try {
            super.delegate.updateCharacterStream(s, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBlob(int i, InputStream inputstream) throws SQLException {
        try {
            super.delegate.updateBlob(i, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateBlob(String s, InputStream inputstream) throws SQLException {
        try {
            super.delegate.updateBlob(s, inputstream);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateClob(int i, Reader reader) throws SQLException {
        try {
            super.delegate.updateClob(i, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateClob(String s, Reader reader) throws SQLException {
        try {
            super.delegate.updateClob(s, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNClob(int i, Reader reader) throws SQLException {
        try {
            super.delegate.updateNClob(i, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateNClob(String s, Reader reader) throws SQLException {
        try {
            super.delegate.updateNClob(s, reader);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Object getObject(int i, Class oclass) throws SQLException {
        try {
            return super.delegate.getObject(i, oclass);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public Object getObject(String s, Class oclass) throws SQLException {
        try {
            return super.delegate.getObject(s, oclass);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateObject(int i, Object object, SQLType sqltype, int j) throws SQLException {
        try {
            super.delegate.updateObject(i, object, sqltype, j);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateObject(String s, Object object, SQLType sqltype, int i) throws SQLException {
        try {
            super.delegate.updateObject(s, object, sqltype, i);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateObject(int i, Object object, SQLType sqltype) throws SQLException {
        try {
            super.delegate.updateObject(i, object, sqltype);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    public void updateObject(String s, Object object, SQLType sqltype) throws SQLException {
        try {
            super.delegate.updateObject(s, object, sqltype);
        } catch (SQLException sqlexception) {
            throw ((ProxyResultSet) this).checkException(sqlexception);
        }
    }

    protected HikariProxyResultSet(ProxyConnection proxyconnection, ProxyStatement proxystatement, ResultSet resultset) {
        super(proxyconnection, proxystatement, resultset);
    }
}
