package com.google.inject.internal.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;

public final class SourceProvider {

    private static final CallerFinder FINDER = loadCallerFinder();
    public static final Object UNKNOWN_SOURCE = "[unknown source]";
    private final SourceProvider parent;
    private final ImmutableSet<String> classNamesToSkip;
    public static final SourceProvider DEFAULT_INSTANCE = new SourceProvider(ImmutableSet.of(SourceProvider.class.getName(), NewThrowableFinder.class.getName()));

    private SourceProvider(Iterable<String> classesToSkip) {
        this((SourceProvider) null, classesToSkip);
    }

    private SourceProvider(SourceProvider parent, Iterable<String> classesToSkip) {
        super();
        this.parent = parent;
        ImmutableSet.Builder<String> classNamesToSkipBuilder = ImmutableSet.builder();

        for (String classToSkip : classesToSkip) {
            if (parent == null || !parent.shouldBeSkipped(classToSkip)) {
                classNamesToSkipBuilder.add(classToSkip);
            }
        }

        this.classNamesToSkip = classNamesToSkipBuilder.build();
    }

    public SourceProvider plusSkippedClasses(Class<?>... moreClassesToSkip) {
        return new SourceProvider(this, asStrings(moreClassesToSkip));
    }

    private boolean shouldBeSkipped(String className) {
        return this.parent != null && this.parent.shouldBeSkipped(className) || this.classNamesToSkip.contains(className);
    }

    private static List<String> asStrings(Class<?>... classes) {
        List<String> strings = Lists.newArrayList();

        for (Class<?> c : classes) {
            strings.add(c.getName());
        }

        return strings;
    }

    public StackTraceElement getCaller() {
        return SourceProvider.FINDER.findCaller(this::shouldBeSkipped);
    }

    public Object getFromClassNames(List<String> moduleClassNames) {
        Preconditions.checkNotNull(moduleClassNames, "The list of module class names cannot be null.");

        for (String moduleClassName : moduleClassNames) {
            if (!this.shouldBeSkipped(moduleClassName)) {
                return new StackTraceElement(moduleClassName, "configure", (String) null, -1);
            }
        }

        return SourceProvider.UNKNOWN_SOURCE;
    }

    private static CallerFinder loadCallerFinder() {
        return new NewThrowableFinder();
    }
}
