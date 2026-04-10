package com.zaxxer.hikari.util;

import com.zaxxer.hikari.HikariConfig;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertyElf {

    private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is)[A-Z].+");

    private PropertyElf() {
        super();
    }

    public static void setTargetFromProperties(Object target, Properties properties) {
        if (target != null && properties != null) {
            List<Method> methods = Arrays.asList(target.getClass().getMethods());

            properties.forEach((keyx, valuex) -> {
                if (target instanceof HikariConfig && keyx.toString().startsWith("dataSource.")) {
                    ((HikariConfig) target).addDataSourceProperty(keyx.toString().substring("dataSource.".length()), valuex);
                } else {
                    setProperty(target, keyx.toString(), valuex, methods);
                }

            });
        }
    }

    public static Set<String> getPropertyNames(Class<?> targetClass) {
        HashSet<String> set = new HashSet();
        Matcher matcher = PropertyElf.GETTER_PATTERN.matcher("");

        for (Method method : targetClass.getMethods()) {
            String name = method.getName();

            if (method.getParameterTypes().length == 0 && matcher.reset(name).matches()) {
                name = name.replaceFirst("(get|is)", "");

                try {
                    if (targetClass.getMethod("set" + name, method.getReturnType()) != null) {
                        char c0 = Character.toLowerCase(name.charAt(0));

                        name = c0 + name.substring(1);
                        set.add(name);
                    }
                } catch (Exception exception) {
                    ;
                }
            }
        }

        return set;
    }

    public static Object getProperty(String propName, Object target) {
        try {
            String s1 = propName.substring(0, 1).toUpperCase(Locale.ENGLISH);
            String capitalized = "get" + s1 + propName.substring(1);
            Method method = target.getClass().getMethod(capitalized);

            return method.invoke(target);
        } catch (Exception exception) {
            try {
                String s2 = propName.substring(0, 1).toUpperCase(Locale.ENGLISH);
                String capitalized = "is" + s2 + propName.substring(1);
                Method method = target.getClass().getMethod(capitalized);

                return method.invoke(target);
            } catch (Exception exception1) {
                return null;
            }
        }
    }

    public static Properties copyProperties(Properties props) {
        Properties copy = new Properties();

        props.forEach((keyx, valuex) -> {
            copy.setProperty(keyx.toString(), valuex.toString());
        });
        return copy;
    }

    private static void setProperty(Object target, String propName, Object propValue, List<Method> methods) {
        Logger logger = LoggerFactory.getLogger(PropertyElf.class);
        String s1 = propName.substring(0, 1).toUpperCase(Locale.ENGLISH);
        String methodName = "set" + s1 + propName.substring(1);
        Method writeMethod = (Method) methods.stream().filter((mx) -> {
            return mx.getName().equals(methodName) && mx.getParameterCount() == 1;
        }).findFirst().orElse((Object) null);

        if (writeMethod == null) {
            String methodName2 = "set" + propName.toUpperCase(Locale.ENGLISH);

            writeMethod = (Method) methods.stream().filter((mx) -> {
                return mx.getName().equals(methodName2) && mx.getParameterCount() == 1;
            }).findFirst().orElse((Object) null);
        }

        if (writeMethod == null) {
            logger.error("Property {} does not exist on target {}", (Object) propName, (Object) target.getClass());
            throw new RuntimeException(String.format("Property %s does not exist on target %s", propName, target.getClass()));
        } else {
            try {
                Class<?> paramClass = writeMethod.getParameterTypes()[0];

                if (paramClass == Integer.TYPE) {
                    writeMethod.invoke(target, Integer.parseInt(propValue.toString()));
                } else if (paramClass == Long.TYPE) {
                    writeMethod.invoke(target, Long.parseLong(propValue.toString()));
                } else if (paramClass == Short.TYPE) {
                    writeMethod.invoke(target, Short.parseShort(propValue.toString()));
                } else if (paramClass != Boolean.TYPE && paramClass != Boolean.class) {
                    if (paramClass.isArray() && Character.TYPE.isAssignableFrom(paramClass.getComponentType())) {
                        writeMethod.invoke(target, propValue.toString().toCharArray());
                    } else if (paramClass == String.class) {
                        writeMethod.invoke(target, propValue.toString());
                    } else {
                        try {
                            logger.debug("Try to create a new instance of \"{}\"", propValue);
                            writeMethod.invoke(target, Class.forName(propValue.toString()).getDeclaredConstructor().newInstance());
                        } catch (ClassNotFoundException | InstantiationException instantiationexception) {
                            logger.debug("Class \"{}\" not found or could not instantiate it (Default constructor)", propValue);
                            writeMethod.invoke(target, propValue);
                        }
                    }
                } else {
                    writeMethod.invoke(target, Boolean.parseBoolean(propValue.toString()));
                }

            } catch (Exception exception) {
                logger.error("Failed to set property {} on target {}", propName, target.getClass(), exception);
                throw new RuntimeException(exception);
            }
        }
    }
}
