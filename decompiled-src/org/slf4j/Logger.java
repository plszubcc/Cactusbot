package org.slf4j;

public interface Logger {

    String ROOT_LOGGER_NAME = "ROOT";

    String getName();

    boolean isTraceEnabled();

    void trace(String s);

    void trace(String s, Object object);

    void trace(String s, Object object, Object object1);

    void trace(String s, Object... aobject);

    void trace(String s, Throwable throwable);

    boolean isTraceEnabled(Marker marker);

    void trace(Marker marker, String s);

    void trace(Marker marker, String s, Object object);

    void trace(Marker marker, String s, Object object, Object object1);

    void trace(Marker marker, String s, Object... aobject);

    void trace(Marker marker, String s, Throwable throwable);

    boolean isDebugEnabled();

    void debug(String s);

    void debug(String s, Object object);

    void debug(String s, Object object, Object object1);

    void debug(String s, Object... aobject);

    void debug(String s, Throwable throwable);

    boolean isDebugEnabled(Marker marker);

    void debug(Marker marker, String s);

    void debug(Marker marker, String s, Object object);

    void debug(Marker marker, String s, Object object, Object object1);

    void debug(Marker marker, String s, Object... aobject);

    void debug(Marker marker, String s, Throwable throwable);

    boolean isInfoEnabled();

    void info(String s);

    void info(String s, Object object);

    void info(String s, Object object, Object object1);

    void info(String s, Object... aobject);

    void info(String s, Throwable throwable);

    boolean isInfoEnabled(Marker marker);

    void info(Marker marker, String s);

    void info(Marker marker, String s, Object object);

    void info(Marker marker, String s, Object object, Object object1);

    void info(Marker marker, String s, Object... aobject);

    void info(Marker marker, String s, Throwable throwable);

    boolean isWarnEnabled();

    void warn(String s);

    void warn(String s, Object object);

    void warn(String s, Object... aobject);

    void warn(String s, Object object, Object object1);

    void warn(String s, Throwable throwable);

    boolean isWarnEnabled(Marker marker);

    void warn(Marker marker, String s);

    void warn(Marker marker, String s, Object object);

    void warn(Marker marker, String s, Object object, Object object1);

    void warn(Marker marker, String s, Object... aobject);

    void warn(Marker marker, String s, Throwable throwable);

    boolean isErrorEnabled();

    void error(String s);

    void error(String s, Object object);

    void error(String s, Object object, Object object1);

    void error(String s, Object... aobject);

    void error(String s, Throwable throwable);

    boolean isErrorEnabled(Marker marker);

    void error(Marker marker, String s);

    void error(Marker marker, String s, Object object);

    void error(Marker marker, String s, Object object, Object object1);

    void error(Marker marker, String s, Object... aobject);

    void error(Marker marker, String s, Throwable throwable);
}
