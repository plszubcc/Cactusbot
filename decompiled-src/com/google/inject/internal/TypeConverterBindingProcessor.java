package com.google.inject.internal;

import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.SourceProvider;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeConverterBinding;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

final class TypeConverterBindingProcessor extends AbstractProcessor {

    TypeConverterBindingProcessor(Errors errors) {
        super(errors);
    }

    static void prepareBuiltInConverters(InjectorImpl injector) {
        convertToPrimitiveType(injector, Integer.TYPE, Integer.class);
        convertToPrimitiveType(injector, Long.TYPE, Long.class);
        convertToPrimitiveType(injector, Boolean.TYPE, Boolean.class);
        convertToPrimitiveType(injector, Byte.TYPE, Byte.class);
        convertToPrimitiveType(injector, Short.TYPE, Short.class);
        convertToPrimitiveType(injector, Float.TYPE, Float.class);
        convertToPrimitiveType(injector, Double.TYPE, Double.class);
        convertToClass(injector, Character.class, new TypeConverter() {
            public Object convert(String value, TypeLiteral<?> toType) {
                value = value.trim();
                if (value.length() != 1) {
                    throw new RuntimeException("Length != 1.");
                } else {
                    return value.charAt(0);
                }
            }

            public String toString() {
                return "TypeConverter<Character>";
            }
        });
        convertToClasses(injector, Matchers.subclassesOf(Enum.class), new TypeConverter() {
            public Object convert(String value, TypeLiteral<?> toType) {
                return Enum.valueOf(toType.getRawType(), value);
            }

            public String toString() {
                return "TypeConverter<E extends Enum<E>>";
            }
        });
        internalConvertToTypes(injector, new AbstractMatcher<TypeLiteral<?>>() {
            public boolean matches(TypeLiteral<?> typeLiteral) {
                return typeLiteral.getRawType() == Class.class;
            }

            public String toString() {
                return "Class<?>";
            }
        }, new TypeConverter() {
            public Object convert(String value, TypeLiteral<?> toType) {
                try {
                    return Class.forName(value);
                } catch (ClassNotFoundException classnotfoundexception) {
                    throw new RuntimeException(classnotfoundexception.getMessage());
                }
            }

            public String toString() {
                return "TypeConverter<Class<?>>";
            }
        });
    }

    private static <T> void convertToPrimitiveType(InjectorImpl injector, Class<T> primitiveType, final Class<T> wrapperType) {
        try {
            final Method parser = wrapperType.getMethod("parse" + capitalize(primitiveType.getName()), String.class);
            TypeConverter typeConverter = new TypeConverter() {
                public Object convert(String value, TypeLiteral<?> toType) {
                    try {
                        return parser.invoke((Object) null, value);
                    } catch (IllegalAccessException illegalaccessexception) {
                        throw new AssertionError(illegalaccessexception);
                    } catch (InvocationTargetException invocationtargetexception) {
                        throw new RuntimeException(invocationtargetexception.getTargetException().getMessage());
                    }
                }

                public String toString() {
                    return "TypeConverter<" + wrapperType.getSimpleName() + ">";
                }
            };

            convertToClass(injector, wrapperType, typeConverter);
        } catch (NoSuchMethodException nosuchmethodexception) {
            throw new AssertionError(nosuchmethodexception);
        }
    }

    private static <T> void convertToClass(InjectorImpl injector, Class<T> type, TypeConverter converter) {
        convertToClasses(injector, Matchers.identicalTo(type), converter);
    }

    private static void convertToClasses(InjectorImpl injector, final Matcher<? super Class<?>> typeMatcher, TypeConverter converter) {
        internalConvertToTypes(injector, new AbstractMatcher<TypeLiteral<?>>() {
            public boolean matches(TypeLiteral<?> typeLiteral) {
                Type type = typeLiteral.getType();

                if (!(type instanceof Class)) {
                    return false;
                } else {
                    Class<?> clazz = (Class) type;

                    return typeMatcher.matches(clazz);
                }
            }

            public String toString() {
                return typeMatcher.toString();
            }
        }, converter);
    }

    private static void internalConvertToTypes(InjectorImpl injector, Matcher<? super TypeLiteral<?>> typeMatcher, TypeConverter converter) {
        injector.getBindingData().addConverter(new TypeConverterBinding(SourceProvider.UNKNOWN_SOURCE, typeMatcher, converter));
    }

    public Boolean visit(TypeConverterBinding command) {
        this.injector.getBindingData().addConverter(new TypeConverterBinding(command.getSource(), command.getTypeMatcher(), command.getTypeConverter()));
        return true;
    }

    private static String capitalize(String s) {
        if (s.length() == 0) {
            return s;
        } else {
            char first = s.charAt(0);
            char capitalized = Character.toUpperCase(first);

            return first == capitalized ? s : capitalized + s.substring(1);
        }
    }
}
