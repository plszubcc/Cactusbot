package com.google.inject.internal;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.ErrorDetail;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

final class DuplicateElementError<T> extends InternalErrorDetail<DuplicateElementError<T>> {

    private final Key<Set<T>> setKey;
    private final ImmutableMultimap<T, DuplicateElementError.Element<T>> elements;

    DuplicateElementError(Key<Set<T>> setKey, List<Binding<T>> bindings, T[] values, List<Object> sources) {
        this(setKey, indexElements(bindings, values), sources);
    }

    private DuplicateElementError(Key<Set<T>> setKey, ImmutableMultimap<T, DuplicateElementError.Element<T>> elements, List<Object> sources) {
        super(ErrorId.DUPLICATE_ELEMENT, String.format("Duplicate elements found in Multibinder %s.", Messages.convert(setKey)), sources, (Throwable) null);
        this.setKey = setKey;
        this.elements = elements;
    }

    protected void formatDetail(List<ErrorDetail<?>> others, Formatter formatter) {
        formatter.format("\n%s\n", Messages.bold("Duplicates:"));
        int duplicateIndex = 1;
        UnmodifiableIterator unmodifiableiterator = this.elements.asMap().entrySet().iterator();

        while (unmodifiableiterator.hasNext()) {
            Map.Entry<T, Collection<DuplicateElementError.Element<T>>> entry = (Entry) unmodifiableiterator.next();

            formatter.format("%-2s: ", duplicateIndex++);
            if (((Collection) entry.getValue()).size() > 1) {
                Set<String> valuesAsString = (Set) ((Collection) entry.getValue()).stream().map((elementx) -> {
                    return elementx.value.toString();
                }).collect(Collectors.toSet());

                if (valuesAsString.size() == 1) {
                    formatter.format("Element: %s\n", Messages.redBold((String) valuesAsString.iterator().next()));
                    formatter.format("    Bound at:\n");
                    int index = 1;

                    for (DuplicateElementError.Element<T> element : (Collection) entry.getValue()) {
                        formatter.format("    %-2s: ", index++);
                        this.formatElement(element, formatter);
                    }
                } else {
                    boolean indent = false;

                    for (DuplicateElementError.Element<T> element : (Collection) entry.getValue()) {
                        if (indent) {
                            formatter.format("    ");
                        } else {
                            indent = true;
                        }

                        formatter.format("Element: %s\n", Messages.redBold(element.value.toString()));
                        formatter.format("    Bound at: ");
                        this.formatElement(element, formatter);
                    }
                }
            }
        }

        formatter.format("\n%s\n", Messages.bold("Multibinder declared at:"));
        List<Object> filteredSource = (List) this.getSources().stream().filter((sourcex) -> {
            return sourcex instanceof Dependency ? !((Dependency) sourcex).getKey().equals(this.setKey) : true;
        }).collect(Collectors.toList());

        ErrorFormatter.formatSources(filteredSource, formatter);
    }

    private void formatElement(DuplicateElementError.Element<T> element, Formatter formatter) {
        Object source = element.binding.getSource();

        (new SourceFormatter(source, formatter, true)).format();
    }

    public DuplicateElementError<T> withSources(List<Object> newSources) {
        return new DuplicateElementError<T>(this.setKey, this.elements, newSources);
    }

    static <T> ImmutableMultimap<T, DuplicateElementError.Element<T>> indexElements(List<Binding<T>> bindings, T[] values) {
        ImmutableMultimap.Builder<T, DuplicateElementError.Element<T>> map = ImmutableMultimap.builder();

        for (int i = 0; i < values.length; ++i) {
            map.put(values[i], new DuplicateElementError.Element(values[i], (Binding) bindings.get(i)));
        }

        return map.build();
    }

    static class Element<T> {

        T value;
        Binding<T> binding;

        Element(T value, Binding<T> binding) {
            super();
            this.value = value;
            this.binding = binding;
        }
    }
}
