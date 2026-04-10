package com.google.inject.spi;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.RestrictedBindingSource;
import com.google.inject.internal.Errors;
import com.google.inject.internal.GuiceInternal;
import java.lang.annotation.Annotation;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class BindingSourceRestriction {

    private static final Logger logger = Logger.getLogger(RestrictedBindingSource.class.getName());

    private BindingSourceRestriction() {
        super();
    }

    public static Optional<String> getMissingImplementationSuggestion(GuiceInternal guiceInternal, Key<?> key) {
        Preconditions.checkNotNull(guiceInternal);
        RestrictedBindingSource restriction = getRestriction(key);

        return restriction == null ? Optional.empty() : Optional.of(String.format("\nHint: This key is restricted and cannot be bound directly. Restriction explanation: %s", restriction.explanation()));
    }

    public static ImmutableList<Message> check(GuiceInternal guiceInternal, List<Element> elements) {
        Preconditions.checkNotNull(guiceInternal);
        ImmutableList<Message> errorMessages = check(elements);

        elements.forEach(BindingSourceRestriction::clear);
        return errorMessages;
    }

    private static ImmutableList<Message> check(List<Element> elements) {
        ImmutableList.Builder<Message> errorMessagesBuilder = ImmutableList.builder();

        for (Element element : elements) {
            errorMessagesBuilder.addAll(check(element));
        }

        return errorMessagesBuilder.build();
    }

    private static ImmutableList<Message> check(Element element) {
        return (ImmutableList) element.acceptVisitor(new DefaultElementVisitor<ImmutableList<Message>>() {
            protected ImmutableList<Message> visitOther(Element element) {
                return ImmutableList.of();
            }

            public <T> ImmutableList<Message> visit(Binding<T> binding) {
                Optional<Message> errorMessage = BindingSourceRestriction.check(binding);

                return errorMessage.isPresent() ? ImmutableList.of((Message) errorMessage.get()) : ImmutableList.of();
            }

            public ImmutableList<Message> visit(PrivateElements privateElements) {
                return BindingSourceRestriction.check(privateElements.getElements());
            }
        });
    }

    private static Optional<Message> check(Binding<?> binding) {
        Key<?> key = binding.getKey();
        ElementSource elementSource = (ElementSource) binding.getSource();
        RestrictedBindingSource restriction = getRestriction(key);

        if (restriction == null) {
            return Optional.empty();
        } else {
            ImmutableSet<Class<? extends Annotation>> permits = getAllPermits(elementSource);
            ImmutableSet<Class<? extends Annotation>> acceptablePermits = ImmutableSet.copyOf(restriction.permits());
            Stream stream = permits.stream();

            Objects.requireNonNull(acceptablePermits);
            boolean bindingPermitted = stream.anyMatch(acceptablePermits::contains);

            if (!bindingPermitted && !isExempt(elementSource, restriction.exemptModules())) {
                String violationMessage = getViolationMessage(key, restriction.explanation(), acceptablePermits, key.getAnnotationType() != null);

                if (restriction.restrictionLevel() == RestrictedBindingSource.RestrictionLevel.WARNING) {
                    Formatter sourceFormatter = new Formatter();

                    Errors.formatSource(sourceFormatter, elementSource);
                    BindingSourceRestriction.logger.log(Level.WARNING, violationMessage + "\n" + sourceFormatter);
                    return Optional.empty();
                } else {
                    return Optional.of(new Message(elementSource, violationMessage));
                }
            } else {
                return Optional.empty();
            }
        }
    }

    private static String getViolationMessage(Key<?> key, String explanation, ImmutableSet<Class<? extends Annotation>> acceptablePermits, boolean annotationRestricted) {
        return String.format("Unable to bind key: %s. One of the modules that created this binding has to be annotated with one of %s, because the key's %s is annotated with @RestrictedBindingSource. %s", key, acceptablePermits.stream().map((ax) -> {
            return "@" + ax.getName();
        }).collect(Collectors.toList()), annotationRestricted ? "annotation" : "type", explanation);
    }

    private static ImmutableSet<Class<? extends Annotation>> getAllPermits(ElementSource elementSource) {
        ImmutableSet.Builder<Class<? extends Annotation>> permitsBuilder = ImmutableSet.builder();

        permitsBuilder.addAll(elementSource.moduleSource.getPermitMap().getPermits(elementSource));
        if (elementSource.scanner != null) {
            Stream stream = getPermits(elementSource.scanner.getClass());

            Objects.requireNonNull(permitsBuilder);
            stream.forEach(permitsBuilder::add);
        }

        if (elementSource.getOriginalElementSource() != null && elementSource.trustedOriginalElementSource) {
            permitsBuilder.addAll(getAllPermits(elementSource.getOriginalElementSource()));
        }

        return permitsBuilder.build();
    }

    private static boolean isExempt(ElementSource elementSource, String exemptModulesRegex) {
        if (exemptModulesRegex.isEmpty()) {
            return false;
        } else {
            Pattern exemptModulePattern = Pattern.compile(exemptModulesRegex);

            return StreamSupport.stream(getAllModules(elementSource).spliterator(), false).anyMatch((moduleNamex) -> {
                return exemptModulePattern.matcher(moduleNamex).matches();
            });
        }
    }

    private static Iterable<String> getAllModules(ElementSource elementSource) {
        List<String> modules = elementSource.getModuleClassNames();

        return (Iterable<String>) (elementSource.getOriginalElementSource() != null && elementSource.trustedOriginalElementSource ? Iterables.concat(modules, getAllModules(elementSource.getOriginalElementSource())) : modules);
    }

    private static void clear(Element element) {
        element.acceptVisitor(new DefaultElementVisitor<Void>() {
            protected Void visitOther(Element element) {
                Object source = element.getSource();

                if (source instanceof ElementSource) {
                    BindingSourceRestriction.clear((ElementSource) source);
                }

                return null;
            }

            public Void visit(PrivateElements privateElements) {
                privateElements.getElements().forEach((x$0x) -> {
                    BindingSourceRestriction.clear(x$0x);
                });
                return null;
            }
        });
    }

    private static void clear(ElementSource elementSource) {
        while (elementSource != null) {
            elementSource.moduleSource.getPermitMap().clear();
            elementSource = elementSource.getOriginalElementSource();
        }

    }

    private static RestrictedBindingSource getRestriction(Key<?> key) {
        return key.getAnnotationType() == null ? (RestrictedBindingSource) key.getTypeLiteral().getRawType().getAnnotation(RestrictedBindingSource.class) : (RestrictedBindingSource) key.getAnnotationType().getAnnotation(RestrictedBindingSource.class);
    }

    private static Stream<Class<? extends Annotation>> getPermits(Class<?> clazz) {
        Stream<Annotation> annotations = Arrays.stream(clazz.getAnnotations());

        if (clazz.getAnnotatedSuperclass() != null) {
            annotations = Stream.concat(annotations, Arrays.stream(clazz.getAnnotatedSuperclass().getAnnotations()));
        }

        return annotations.map(Annotation::annotationType).filter((ax) -> {
            return ax.isAnnotationPresent(RestrictedBindingSource.Permit.class);
        });
    }

    static final class PermitMapConstruction {

        final Map<ModuleSource, ImmutableSet<Class<? extends Annotation>>> modulePermits = new HashMap();
        ImmutableSet<Class<? extends Annotation>> currentModulePermits = ImmutableSet.of();
        final Deque<ImmutableSet<Class<? extends Annotation>>> modulePermitsStack = new ArrayDeque();
        final BindingSourceRestriction.PermitMapConstruction.PermitMapImpl permitMap = new BindingSourceRestriction.PermitMapConstruction.PermitMapImpl();

        PermitMapConstruction() {
            super();
        }

        BindingSourceRestriction.PermitMap getPermitMap() {
            return this.permitMap;
        }

        void restoreCurrentModulePermits(ModuleSource moduleSource) {
            this.currentModulePermits = (ImmutableSet) this.modulePermits.get(moduleSource);
        }

        void pushModule(Class<?> module, ModuleSource moduleSource) {
            List<Class<? extends Annotation>> newModulePermits = (List) BindingSourceRestriction.getPermits(module).filter((permitx) -> {
                return !this.currentModulePermits.contains(permitx);
            }).collect(Collectors.toList());

            this.modulePermitsStack.push(this.currentModulePermits);
            if (!newModulePermits.isEmpty()) {
                this.currentModulePermits = ImmutableSet.builder().addAll(this.currentModulePermits).addAll(newModulePermits).build();
            }

            this.modulePermits.put(moduleSource, this.currentModulePermits);
        }

        void popModule() {
            this.currentModulePermits = (ImmutableSet) this.modulePermitsStack.pop();
        }

        void finish() {
            this.permitMap.modulePermits = this.modulePermits;
        }

        @VisibleForTesting
        static boolean isElementSourceCleared(ElementSource elementSource) {
            BindingSourceRestriction.PermitMapConstruction.PermitMapImpl permitMap = (BindingSourceRestriction.PermitMapConstruction.PermitMapImpl) elementSource.moduleSource.getPermitMap();

            return permitMap.modulePermits == null;
        }

        private static final class PermitMapImpl implements BindingSourceRestriction.PermitMap {

            Map<ModuleSource, ImmutableSet<Class<? extends Annotation>>> modulePermits;

            private PermitMapImpl() {
                super();
            }

            public ImmutableSet<Class<? extends Annotation>> getPermits(ElementSource elementSource) {
                return (ImmutableSet) this.modulePermits.get(elementSource.moduleSource);
            }

            public void clear() {
                this.modulePermits = null;
            }
        }
    }

    interface PermitMap {

        ImmutableSet<Class<? extends Annotation>> getPermits(ElementSource elementSource);

        void clear();
    }
}
