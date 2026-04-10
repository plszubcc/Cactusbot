package com.google.inject.name;

import com.google.inject.Binder;
import com.google.inject.Key;
import java.lang.annotation.Annotation;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

public class Names {

    private Names() {
        super();
    }

    public static Named named(String name) {
        return new NamedImpl(name);
    }

    public static void bindProperties(Binder binder, Map<String, String> properties) {
        binder = binder.skipSources(Names.class);

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();

            binder.bind(Key.get(String.class, (Annotation) (new NamedImpl(key)))).toInstance(value);
        }

    }

    public static void bindProperties(Binder binder, Properties properties) {
        binder = binder.skipSources(Names.class);
        Enumeration<?> e = properties.propertyNames();

        while (e.hasMoreElements()) {
            String propertyName = (String) e.nextElement();
            String value = properties.getProperty(propertyName);

            binder.bind(Key.get(String.class, (Annotation) (new NamedImpl(propertyName)))).toInstance(value);
        }

    }
}
