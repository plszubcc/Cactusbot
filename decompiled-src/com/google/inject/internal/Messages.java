package com.google.inject.internal;

import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.inject.Key;
import com.google.inject.internal.util.Classes;
import com.google.inject.spi.ElementSource;
import com.google.inject.spi.ErrorDetail;
import com.google.inject.spi.Message;
import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Messages {

    private static final Collection<Messages.Converter<?>> converters = ImmutableList.of(new Messages.Converter<Class>(Class.class) {
        public String toString(Class c) {
            return c.getName();
        }
    }, new Messages.Converter<Member>(Member.class) {
        public String toString(Member member) {
            return Classes.toString(member);
        }
    }, new Messages.Converter<Key>(Key.class) {
        public String toString(Key key) {
            return key.getAnnotationType() != null ? key.getTypeLiteral() + " annotated with " + (key.getAnnotation() != null ? key.getAnnotation() : key.getAnnotationType()) : key.getTypeLiteral().toString();
        }
    });

    private Messages() {
        super();
    }

    static Message mergeSources(List<Object> sources, Message message) {
        List<Object> messageSources = message.getSources();

        if (!sources.isEmpty() && !messageSources.isEmpty() && Objects.equal(messageSources.get(0), sources.get(sources.size() - 1))) {
            messageSources = messageSources.subList(1, messageSources.size());
        }

        return message.withSource(ImmutableList.builder().addAll(sources).addAll(messageSources).build());
    }

    public static String format(String messageFormat, Object... arguments) {
        for (int i = 0; i < arguments.length; ++i) {
            arguments[i] = convert(arguments[i]);
        }

        return String.format(messageFormat, arguments);
    }

    public static String formatMessages(String heading, Collection<Message> errorMessages) {
        Formatter fmt = (new Formatter()).format(heading).format(":\n\n");
        int index = 1;
        boolean displayCauses = getOnlyCause(errorMessages) == null;
        List<ErrorDetail<?>> remainingErrors = (List) errorMessages.stream().map(Message::getErrorDetail).collect(Collectors.toList());

        for (Map<Equivalence.Wrapper<Throwable>, Integer> causes = Maps.newHashMap(); !remainingErrors.isEmpty(); ++index) {
            ErrorDetail<?> currentError = (ErrorDetail) remainingErrors.get(0);
            Stream stream = remainingErrors.subList(1, remainingErrors.size()).stream();

            java.util.Objects.requireNonNull(currentError);
            Map<Boolean, List<ErrorDetail<?>>> partitionedByMergeable = (Map) stream.collect(Collectors.partitioningBy(currentError::isMergeable));

            remainingErrors = (List) partitionedByMergeable.get(false);
            currentError.format(index, (List) partitionedByMergeable.get(true), fmt);
            Throwable cause = currentError.getCause();

            if (displayCauses && cause != null) {
                Equivalence.Wrapper<Throwable> causeEquivalence = Messages.ThrowableEquivalence.INSTANCE.wrap(cause);

                if (!causes.containsKey(causeEquivalence)) {
                    causes.put(causeEquivalence, index);
                    fmt.format("Caused by: %s", Throwables.getStackTraceAsString(cause));
                } else {
                    int causeIdx = (Integer) causes.get(causeEquivalence);

                    fmt.format("Caused by: %s (same stack trace as error #%s)", cause.getClass().getName(), causeIdx);
                }
            }

            fmt.format("\n");
        }

        if (index == 2) {
            fmt.format("1 error");
        } else {
            fmt.format("%s errors", index - 1);
        }

        return PackageNameCompressor.compressPackagesInMessage(fmt.toString());
    }

    public static Message create(ErrorId errorId, String messageFormat, Object... arguments) {
        return create(errorId, (Throwable) null, messageFormat, arguments);
    }

    public static Message create(ErrorId errorId, Throwable cause, String messageFormat, Object... arguments) {
        return create(errorId, cause, ImmutableList.of(), messageFormat, arguments);
    }

    public static Message create(ErrorId errorId, Throwable cause, List<Object> sources, String messageFormat, Object... arguments) {
        String message = format(messageFormat, arguments);

        return new Message(errorId, sources, message, cause);
    }

    static Object convert(Object o) {
        ElementSource source = null;

        if (o instanceof ElementSource) {
            source = (ElementSource) o;
            o = source.getDeclaringSource();
        }

        return convert(o, source);
    }

    static Object convert(Object o, ElementSource source) {
        for (Messages.Converter<?> converter : Messages.converters) {
            if (converter.appliesTo(o)) {
                return appendModules(converter.convert(o), source);
            }
        }

        return appendModules(o, source);
    }

    private static Object appendModules(Object source, ElementSource elementSource) {
        String modules = SourceFormatter.getModuleStack(elementSource);

        return modules.length() == 0 ? source : source + " (installed by: " + modules + ")";
    }

    public static Throwable getOnlyCause(Collection<Message> messages) {
        Throwable onlyCause = null;

        for (Message message : messages) {
            Throwable messageCause = message.getCause();

            if (messageCause != null) {
                if (onlyCause != null && !Messages.ThrowableEquivalence.INSTANCE.equivalent(onlyCause, messageCause)) {
                    return null;
                }

                onlyCause = messageCause;
            }
        }

        return onlyCause;
    }

    private static final String formatText(String text, Messages.FormatOptions... options) {
        return !InternalFlags.enableColorizeErrorMessages() ? text : String.format("%s%s%s", Arrays.stream(options).map((optionx) -> {
            return optionx.ansiCode;
        }).collect(Collectors.joining()), text, Messages.FormatOptions.RESET.ansiCode);
    }

    public static final String bold(String text) {
        return formatText(text, Messages.FormatOptions.BOLD);
    }

    public static final String redBold(String text) {
        return formatText(text, Messages.FormatOptions.RED, Messages.FormatOptions.BOLD);
    }

    public static final String underline(String text) {
        return formatText(text, Messages.FormatOptions.UNDERLINE);
    }

    public static final String faint(String text) {
        return formatText(text, Messages.FormatOptions.FAINT);
    }

    private abstract static class Converter<T> {

        final Class<T> type;

        Converter(Class<T> type) {
            super();
            this.type = type;
        }

        boolean appliesTo(Object o) {
            return o != null && this.type.isAssignableFrom(o.getClass());
        }

        String convert(Object o) {
            return this.toString(this.type.cast(o));
        }

        abstract String toString(T t);
    }

    private static final class ThrowableEquivalence extends Equivalence<Throwable> {

        static final Messages.ThrowableEquivalence INSTANCE = new Messages.ThrowableEquivalence();

        private ThrowableEquivalence() {
            super();
        }

        protected boolean doEquivalent(Throwable a, Throwable b) {
            return a.getClass().equals(b.getClass()) && Objects.equal(a.getMessage(), b.getMessage()) && Arrays.equals(a.getStackTrace(), b.getStackTrace()) && this.equivalent(a.getCause(), b.getCause());
        }

        protected int doHash(Throwable t) {
            return Objects.hashCode(new Object[]{t.getClass().hashCode(), t.getMessage(), this.hash(t.getCause())});
        }
    }

    private static enum FormatOptions {

        RED("\u001b[31m"), BOLD("\u001b[1m"), FAINT("\u001b[2m"), ITALIC("\u001b[3m"), UNDERLINE("\u001b[4m"), RESET("\u001b[0m");

        private final String ansiCode;

        private FormatOptions(String ansiCode) {
            this.ansiCode = ansiCode;
        }
    }
}
