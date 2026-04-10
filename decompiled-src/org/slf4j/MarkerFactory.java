package org.slf4j;

import org.slf4j.helpers.BasicMarkerFactory;
import org.slf4j.helpers.Util;
import org.slf4j.impl.StaticMarkerBinder;

public class MarkerFactory {

    static IMarkerFactory MARKER_FACTORY;

    private MarkerFactory() {
        super();
    }

    private static IMarkerFactory bwCompatibleGetMarkerFactoryFromBinder() throws NoClassDefFoundError {
        try {
            return StaticMarkerBinder.getSingleton().getMarkerFactory();
        } catch (NoSuchMethodError nosuchmethoderror) {
            return StaticMarkerBinder.SINGLETON.getMarkerFactory();
        }
    }

    public static Marker getMarker(String name) {
        return MarkerFactory.MARKER_FACTORY.getMarker(name);
    }

    public static Marker getDetachedMarker(String name) {
        return MarkerFactory.MARKER_FACTORY.getDetachedMarker(name);
    }

    public static IMarkerFactory getIMarkerFactory() {
        return MarkerFactory.MARKER_FACTORY;
    }

    static {
        try {
            MarkerFactory.MARKER_FACTORY = bwCompatibleGetMarkerFactoryFromBinder();
        } catch (NoClassDefFoundError noclassdeffounderror) {
            MarkerFactory.MARKER_FACTORY = new BasicMarkerFactory();
        } catch (Exception exception) {
            Util.report("Unexpected failure while binding MarkerFactory", exception);
        }

    }
}
