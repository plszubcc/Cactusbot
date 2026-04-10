package com.google.inject.internal;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.logging.Logger;

public final class InternalFlags {

    private static final Logger logger = Logger.getLogger(InternalFlags.class.getName());
    private static final InternalFlags.IncludeStackTraceOption INCLUDE_STACK_TRACES = (InternalFlags.IncludeStackTraceOption) getSystemOption("guice_include_stack_traces", InternalFlags.IncludeStackTraceOption.ONLY_FOR_DECLARING_SOURCE);
    private static final InternalFlags.CustomClassLoadingOption CUSTOM_CLASS_LOADING = (InternalFlags.CustomClassLoadingOption) getSystemOption("guice_custom_class_loading", InternalFlags.CustomClassLoadingOption.BRIDGE, InternalFlags.CustomClassLoadingOption.OFF);
    private static final InternalFlags.NullableProvidesOption NULLABLE_PROVIDES = (InternalFlags.NullableProvidesOption) getSystemOption("guice_check_nullable_provides_params", InternalFlags.NullableProvidesOption.ERROR);
    private static final InternalFlags.BytecodeGenOption BYTECODE_GEN_OPTION = (InternalFlags.BytecodeGenOption) getSystemOption("guice_bytecode_gen_option", InternalFlags.BytecodeGenOption.ENABLED);
    private static final InternalFlags.ColorizeOption COLORIZE_OPTION = (InternalFlags.ColorizeOption) getSystemOption("guice_colorize_error_messages", InternalFlags.ColorizeOption.OFF);

    public static InternalFlags.IncludeStackTraceOption getIncludeStackTraceOption() {
        return InternalFlags.INCLUDE_STACK_TRACES;
    }

    public static InternalFlags.CustomClassLoadingOption getCustomClassLoadingOption() {
        return InternalFlags.CUSTOM_CLASS_LOADING;
    }

    public static InternalFlags.NullableProvidesOption getNullableProvidesOption() {
        return InternalFlags.NULLABLE_PROVIDES;
    }

    public static boolean isBytecodeGenEnabled() {
        return InternalFlags.BYTECODE_GEN_OPTION == InternalFlags.BytecodeGenOption.ENABLED;
    }

    public static boolean enableColorizeErrorMessages() {
        return InternalFlags.COLORIZE_OPTION.enabled();
    }

    private static <T extends Enum<T>> T getSystemOption(String name, T defaultValue) {
        return (T) getSystemOption(name, defaultValue, defaultValue);
    }

    private static <T extends Enum<T>> T getSystemOption(final String name, T defaultValue, T secureValue) {
        Class<T> enumType = defaultValue.getDeclaringClass();
        String value = null;

        try {
            value = (String) AccessController.doPrivileged(new PrivilegedAction<String>() {
                public String run() {
                    return System.getProperty(name);
                }
            });
            return (T) (value != null && value.length() > 0 ? Enum.valueOf(enumType, value) : defaultValue);
        } catch (SecurityException securityexception) {
            return secureValue;
        } catch (IllegalArgumentException illegalargumentexception) {
            InternalFlags.logger.warning(value + " is not a valid flag value for " + name + ".  Values must be one of " + Arrays.asList((Enum[]) enumType.getEnumConstants()));
            return defaultValue;
        }
    }

    private InternalFlags() {
        super();
    }

    public static enum IncludeStackTraceOption {

        OFF, ONLY_FOR_DECLARING_SOURCE;

        private IncludeStackTraceOption() {}
    }

    public static enum CustomClassLoadingOption {

        OFF, ANONYMOUS, BRIDGE, CHILD;

        private CustomClassLoadingOption() {}
    }

    public static enum NullableProvidesOption {

        IGNORE, WARN, ERROR;

        private NullableProvidesOption() {}
    }

    public static enum BytecodeGenOption {

        DISABLED, ENABLED;

        private BytecodeGenOption() {}
    }

    public static enum ColorizeOption {

        AUTO {
            boolean enabled() {
                return System.console() != null && System.getenv("TERM") != null;
            }
        },
        ON {
            boolean enabled() {
                return true;
            }
        },
        OFF {
            boolean enabled() {
                return false;
            }
        };

        private ColorizeOption() {}

        abstract boolean enabled();
    }
}
