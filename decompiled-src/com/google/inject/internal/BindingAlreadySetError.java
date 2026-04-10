package com.google.inject.internal;

import com.google.common.collect.ImmutableList;
import com.google.inject.Binding;
import com.google.inject.spi.ErrorDetail;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.stream.Collectors;

final class BindingAlreadySetError extends InternalErrorDetail<BindingAlreadySetError> {

    private final Binding<?> binding;
    private final Binding<?> original;

    BindingAlreadySetError(Binding<?> binding, Binding<?> original, List<Object> sources) {
        super(ErrorId.BINDING_ALREADY_SET, String.format("%s was bound multiple times.", Messages.convert(binding.getKey())), sources, (Throwable) null);
        this.binding = binding;
        this.original = original;
    }

    public boolean isMergeable(ErrorDetail<?> otherError) {
        return otherError instanceof BindingAlreadySetError && ((BindingAlreadySetError) otherError).binding.getKey().equals(this.binding.getKey());
    }

    public void formatDetail(List<ErrorDetail<?>> mergeableErrors, Formatter formatter) {
        List<List<Object>> sourcesList = new ArrayList();

        sourcesList.add(ImmutableList.of(this.original.getSource()));
        sourcesList.add(ImmutableList.of(this.binding.getSource()));
        sourcesList.addAll((Collection) mergeableErrors.stream().map((ex) -> {
            return ((BindingAlreadySetError) ex).binding.getSource();
        }).map(ImmutableList::of).collect(Collectors.toList()));
        formatter.format("\n%s\n", Messages.bold("Bound at:"));

        for (int i = 0; i < sourcesList.size(); ++i) {
            ErrorFormatter.formatSources(i + 1, (List) sourcesList.get(i), formatter);
        }

    }

    public BindingAlreadySetError withSources(List<Object> newSources) {
        return new BindingAlreadySetError(this.binding, this.original, newSources);
    }
}
