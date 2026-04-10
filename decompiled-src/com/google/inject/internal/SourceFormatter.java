package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.internal.util.Classes;
import com.google.inject.internal.util.StackTraceElements;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ElementSource;
import com.google.inject.spi.InjectionPoint;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Formatter;
import java.util.List;

final class SourceFormatter {

    static final String INDENT = Strings.repeat(" ", 5);
    private final Object source;
    private final Formatter formatter;
    private final boolean omitPreposition;
    private final String moduleStack;

    SourceFormatter(Object source, Formatter formatter, boolean omitPreposition) {
        super();
        if (source instanceof ElementSource) {
            ElementSource elementSource = (ElementSource) source;

            this.source = elementSource.getDeclaringSource();
            this.moduleStack = getModuleStack(elementSource);
        } else {
            this.source = source;
            this.moduleStack = "";
        }

        this.formatter = formatter;
        this.omitPreposition = omitPreposition;
    }

    void format() {
        boolean appendModuleSource = !this.moduleStack.isEmpty();

        if (this.source instanceof Dependency) {
            this.formatDependency((Dependency) this.source);
        } else if (this.source instanceof InjectionPoint) {
            this.formatInjectionPoint((Dependency) null, (InjectionPoint) this.source);
        } else if (this.source instanceof Class) {
            this.formatter.format("%s%s\n", this.preposition("at "), StackTraceElements.forType((Class) this.source));
        } else if (this.source instanceof Member) {
            this.formatMember((Member) this.source);
        } else if (this.source instanceof TypeLiteral) {
            this.formatter.format("%s%s\n", this.preposition("while locating "), this.source);
        } else if (this.source instanceof Key) {
            this.formatKey((Key) this.source);
        } else if (this.source instanceof Thread) {
            appendModuleSource = false;
            this.formatter.format("%s%s\n", this.preposition("in thread "), this.source);
        } else {
            this.formatter.format("%s%s\n", this.preposition("at "), this.source);
        }

        if (appendModuleSource) {
            this.formatter.format("%s \\_ installed by: %s\n", SourceFormatter.INDENT, this.moduleStack);
        }

    }

    private String preposition(String prepostition) {
        return this.omitPreposition ? "" : prepostition;
    }

    private void formatDependency(Dependency<?> dependency) {
        InjectionPoint injectionPoint = dependency.getInjectionPoint();

        if (injectionPoint != null) {
            this.formatInjectionPoint(dependency, injectionPoint);
        } else {
            this.formatKey(dependency.getKey());
        }

    }

    private void formatKey(Key<?> key) {
        this.formatter.format("%s%s\n", this.preposition("while locating "), Messages.convert(key));
    }

    private void formatMember(Member member) {
        this.formatter.format("%s%s\n", this.preposition("at "), StackTraceElements.forMember(member));
    }

    private void formatInjectionPoint(Dependency<?> dependency, InjectionPoint injectionPoint) {
        Member member = injectionPoint.getMember();
        Class<? extends Member> memberType = Classes.memberType(member);

        this.formatMember(injectionPoint.getMember());
        if (memberType == Field.class) {
            this.formatter.format("%s \\_ for field %s\n", SourceFormatter.INDENT, Messages.redBold(member.getName()));
        } else if (dependency != null) {
            this.formatter.format("%s \\_ for %s\n", SourceFormatter.INDENT, getParameterName(dependency));
        }

    }

    static String getModuleStack(ElementSource elementSource) {
        if (elementSource == null) {
            return "";
        } else {
            List<String> modules = Lists.newArrayList(elementSource.getModuleClassNames());

            while (elementSource.getOriginalElementSource() != null) {
                elementSource = elementSource.getOriginalElementSource();
                modules.addAll(0, elementSource.getModuleClassNames());
            }

            return modules.size() <= 1 ? "" : String.join(" -> ", Lists.reverse(modules));
        }
    }

    static String getParameterName(Dependency<?> dependency) {
        int parameterIndex = dependency.getParameterIndex();
        int ordinal = parameterIndex + 1;
        Member member = dependency.getInjectionPoint().getMember();
        Parameter parameter = null;

        if (member instanceof Constructor) {
            parameter = ((Constructor) member).getParameters()[parameterIndex];
        } else if (member instanceof Method) {
            parameter = ((Method) member).getParameters()[parameterIndex];
        }

        String parameterName = "";

        if (parameter != null && parameter.isNamePresent()) {
            parameterName = parameter.getName();
        }

        return String.format("%s%s parameter%s", ordinal, getOrdinalSuffix(ordinal), parameterName.isEmpty() ? "" : " " + Messages.redBold(parameterName));
    }

    private static String getOrdinalSuffix(int ordinal) {
        Preconditions.checkArgument(ordinal >= 0);
        if (ordinal / 10 % 10 == 1) {
            return "th";
        } else {
            switch (ordinal % 10) {
                case 1:
                    return "st";
                case 2:
                    return "nd";
                case 3:
                    return "rd";
                default:
                    return "th";
            }
        }
    }
}
