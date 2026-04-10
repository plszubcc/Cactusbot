package org.slf4j;

public interface IMarkerFactory {

    Marker getMarker(String s);

    boolean exists(String s);

    boolean detachMarker(String s);

    Marker getDetachedMarker(String s);
}
