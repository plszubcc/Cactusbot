package com.google.inject.internal.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class StackTraceElements {

    private static final StackTraceElement[] EMPTY_STACK_TRACE = new StackTraceElement[0];
    private static final StackTraceElements.InMemoryStackTraceElement[] EMPTY_INMEMORY_STACK_TRACE = new StackTraceElements.InMemoryStackTraceElement[0];
    static final LoadingCache<Class<?>, LineNumbers> lineNumbersCache = CacheBuilder.newBuilder().weakKeys().softValues().build(new CacheLoader<Class<?>, LineNumbers>() {
        public LineNumbers load(Class<?> key) {
            try {
                return new LineNumbers(key);
            } catch (IOException ioexception) {
                throw new RuntimeException(ioexception);
            }
        }
    });
    private static final ConcurrentMap<StackTraceElements.InMemoryStackTraceElement, StackTraceElements.InMemoryStackTraceElement> elementCache = new ConcurrentHashMap();
    private static final ConcurrentMap<String, String> stringCache = new ConcurrentHashMap();
    private static final String UNKNOWN_SOURCE = "Unknown Source";

    public static Object forMember(Member member) {
        if (member == null) {
            return SourceProvider.UNKNOWN_SOURCE;
        } else {
            Class<?> declaringClass = member.getDeclaringClass();
            LineNumbers lineNumbers = (LineNumbers) StackTraceElements.lineNumbersCache.getUnchecked(declaringClass);
            String fileName = lineNumbers.getSource();
            Integer lineNumberOrNull = lineNumbers.getLineNumber(member);
            int lineNumber = lineNumberOrNull == null ? lineNumbers.getFirstLine() : lineNumberOrNull;
            Class<? extends Member> memberType = Classes.memberType(member);
            String memberName = memberType == Constructor.class ? "<init>" : member.getName();

            return new StackTraceElement(declaringClass.getName(), memberName, fileName, lineNumber);
        }
    }

    public static Object forType(Class<?> implementation) {
        LineNumbers lineNumbers = (LineNumbers) StackTraceElements.lineNumbersCache.getUnchecked(implementation);
        int lineNumber = lineNumbers.getFirstLine();
        String fileName = lineNumbers.getSource();

        return new StackTraceElement(implementation.getName(), "class", fileName, lineNumber);
    }

    public static void clearCache() {
        StackTraceElements.elementCache.clear();
        StackTraceElements.stringCache.clear();
    }

    public static StackTraceElements.InMemoryStackTraceElement[] convertToInMemoryStackTraceElement(StackTraceElement[] stackTraceElements) {
        if (stackTraceElements.length == 0) {
            return StackTraceElements.EMPTY_INMEMORY_STACK_TRACE;
        } else {
            StackTraceElements.InMemoryStackTraceElement[] inMemoryStackTraceElements = new StackTraceElements.InMemoryStackTraceElement[stackTraceElements.length];

            for (int i = 0; i < stackTraceElements.length; ++i) {
                inMemoryStackTraceElements[i] = weakIntern(new StackTraceElements.InMemoryStackTraceElement(stackTraceElements[i]));
            }

            return inMemoryStackTraceElements;
        }
    }

    public static StackTraceElement[] convertToStackTraceElement(StackTraceElements.InMemoryStackTraceElement[] inMemoryStackTraceElements) {
        if (inMemoryStackTraceElements.length == 0) {
            return StackTraceElements.EMPTY_STACK_TRACE;
        } else {
            StackTraceElement[] stackTraceElements = new StackTraceElement[inMemoryStackTraceElements.length];

            for (int i = 0; i < inMemoryStackTraceElements.length; ++i) {
                String declaringClass = inMemoryStackTraceElements[i].getClassName();
                String methodName = inMemoryStackTraceElements[i].getMethodName();
                int lineNumber = inMemoryStackTraceElements[i].getLineNumber();

                stackTraceElements[i] = new StackTraceElement(declaringClass, methodName, "Unknown Source", lineNumber);
            }

            return stackTraceElements;
        }
    }

    private static StackTraceElements.InMemoryStackTraceElement weakIntern(StackTraceElements.InMemoryStackTraceElement inMemoryStackTraceElement) {
        StackTraceElements.InMemoryStackTraceElement cached = (StackTraceElements.InMemoryStackTraceElement) StackTraceElements.elementCache.get(inMemoryStackTraceElement);

        if (cached != null) {
            return cached;
        } else {
            inMemoryStackTraceElement = new StackTraceElements.InMemoryStackTraceElement(weakIntern(inMemoryStackTraceElement.getClassName()), weakIntern(inMemoryStackTraceElement.getMethodName()), inMemoryStackTraceElement.getLineNumber());
            StackTraceElements.elementCache.put(inMemoryStackTraceElement, inMemoryStackTraceElement);
            return inMemoryStackTraceElement;
        }
    }

    private static String weakIntern(String s) {
        String cached = (String) StackTraceElements.stringCache.get(s);

        if (cached != null) {
            return cached;
        } else {
            StackTraceElements.stringCache.put(s, s);
            return s;
        }
    }

    private StackTraceElements() {
        super();
    }

    public static class InMemoryStackTraceElement {

        private final String declaringClass;
        private final String methodName;
        private final int lineNumber;

        InMemoryStackTraceElement(StackTraceElement ste) {
            this(ste.getClassName(), ste.getMethodName(), ste.getLineNumber());
        }

        InMemoryStackTraceElement(String declaringClass, String methodName, int lineNumber) {
            super();
            this.declaringClass = declaringClass;
            this.methodName = methodName;
            this.lineNumber = lineNumber;
        }

        String getClassName() {
            return this.declaringClass;
        }

        String getMethodName() {
            return this.methodName;
        }

        int getLineNumber() {
            return this.lineNumber;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (!(obj instanceof StackTraceElements.InMemoryStackTraceElement)) {
                return false;
            } else {
                StackTraceElements.InMemoryStackTraceElement e = (StackTraceElements.InMemoryStackTraceElement) obj;

                return e.declaringClass.equals(this.declaringClass) && e.lineNumber == this.lineNumber && this.methodName.equals(e.methodName);
            }
        }

        public int hashCode() {
            int result = 31 * this.declaringClass.hashCode() + this.methodName.hashCode();

            result = 31 * result + this.lineNumber;
            return result;
        }

        public String toString() {
            return this.declaringClass + "." + this.methodName + "(" + this.lineNumber + ")";
        }
    }
}
