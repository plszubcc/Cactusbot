package com.zaxxer.hikari.metrics;

public interface MetricsTrackerFactory {

    IMetricsTracker create(String s, PoolStats poolstats);
}
