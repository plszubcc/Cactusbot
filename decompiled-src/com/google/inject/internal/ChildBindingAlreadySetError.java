package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Streams;
import com.google.common.collect.UnmodifiableIterator;
import com.google.inject.Key;
import com.google.inject.spi.ErrorDetail;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.stream.Collectors;

final class ChildBindingAlreadySetError extends InternalErrorDetail<ChildBindingAlreadySetError> {

    private final Key<?> key;
    private final ImmutableList<Object> existingSources;

    ChildBindingAlreadySetError(Key<?> key, Iterable<Object> existingSoruces, List<Object> sources) {
        super(ErrorId.CHILD_BINDING_ALREADY_SET, String.format("Unable to create binding for %s because it was already configured on one or more child injectors or private modules.", Messages.convert(key)), sources, (Throwable) null);
        this.key = key;
        this.existingSources = ImmutableList.copyOf((Collection) Streams.stream(existingSoruces).map((sourcex) -> {
            return sourcex == null ? "" : sourcex;
        }).collect(Collectors.toList()));
    }

    public boolean isMergeable(ErrorDetail<?> otherError) {
        return otherError instanceof ChildBindingAlreadySetError && ((ChildBindingAlreadySetError) otherError).key.equals(this.key);
    }

    public void formatDetail(List<ErrorDetail<?>> mergeableErrors, Formatter formatter) {
        formatter.format("\n%s\n", Messages.bold("Bound at:"));
        int index = 1;
        UnmodifiableIterator unmodifiableiterator = this.existingSources.iterator();

        while (unmodifiableiterator.hasNext()) {
            Object source = unmodifiableiterator.next();

            formatter.format("%-2s: ", index++);
            if (source.equals("")) {
                formatter.format("as a just-in-time binding\n");
            } else {
                (new SourceFormatter(source, formatter, true)).format();
            }
        }

        List<List<Object>> sourcesList = new ArrayList();

        sourcesList.add(this.getSources());
        mergeableErrors.forEach((errorx) -> {
            sourcesList.add(errorx.getSources());
        });
        List<List<Object>> filteredSources = (List) sourcesList.stream().map(this::trimSource).filter((listx) -> {
            return !listx.isEmpty();
        }).collect(Collectors.toList());

        if (!filteredSources.isEmpty()) {
            formatter.format("\n%s\n", Messages.bold("Requested by:"));

            for (int i = 0; i < sourcesList.size(); ++i) {
                ErrorFormatter.formatSources(i + 1, (List) sourcesList.get(i), formatter);
            }
        }

    }

    public ChildBindingAlreadySetError withSources(List<Object> newSources) {
        return new ChildBindingAlreadySetError(this.key, this.existingSources, newSources);
    }

    private List<Object> trimSource(List<Object> sources) {
        return (List) sources.stream().filter((sourcex) -> {
            return !sourcex.equals(this.key);
        }).collect(Collectors.toList());
    }
}
