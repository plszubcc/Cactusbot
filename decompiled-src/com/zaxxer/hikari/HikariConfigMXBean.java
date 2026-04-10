package com.zaxxer.hikari;

public interface HikariConfigMXBean {

    long getConnectionTimeout();

    void setConnectionTimeout(long i);

    long getValidationTimeout();

    void setValidationTimeout(long i);

    long getIdleTimeout();

    void setIdleTimeout(long i);

    long getLeakDetectionThreshold();

    void setLeakDetectionThreshold(long i);

    long getMaxLifetime();

    void setMaxLifetime(long i);

    int getMinimumIdle();

    void setMinimumIdle(int i);

    int getMaximumPoolSize();

    void setMaximumPoolSize(int i);

    void setPassword(String s);

    void setUsername(String s);

    String getPoolName();

    String getCatalog();

    void setCatalog(String s);
}
