package org.slf4j.spi;

import java.util.Map;

public interface MDCAdapter {

    void put(String s, String s1);

    String get(String s);

    void remove(String s);

    void clear();

    Map<String, String> getCopyOfContextMap();

    void setContextMap(Map<String, String> map);
}
