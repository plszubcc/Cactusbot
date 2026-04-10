package com.google.inject.internal;

import com.google.common.collect.Lists;
import com.google.inject.spi.ErrorDetail;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

final class ScopeNotFoundError extends InternalErrorDetail<ScopeNotFoundError> {

    private final Class<? extends Annotation> scopeAnnotation;

    ScopeNotFoundError(Class<? extends Annotation> scopeAnnotation, List<Object> sources) {
        super(ErrorId.SCOPE_NOT_FOUND, String.format("No scope is bound to %s.", Messages.convert(scopeAnnotation)), sources, (Throwable) null);
        this.scopeAnnotation = scopeAnnotation;
    }

    public boolean isMergeable(ErrorDetail<?> other) {
        return other instanceof ScopeNotFoundError && ((ScopeNotFoundError) other).scopeAnnotation.equals(this.scopeAnnotation);
    }

    protected void formatDetail(List<ErrorDetail<?>> mergeableErrors, Formatter formatter) {
        List<List<Object>> sourcesSet = new ArrayList();

        sourcesSet.add(this.getSources());
        Stream stream = mergeableErrors.stream().map(ErrorDetail::getSources);

        Objects.requireNonNull(sourcesSet);
        stream.forEach(sourcesSet::add);
        formatter.format("\n%s\n", "Used at:");
        int sourceListIndex = 1;

        for (List<Object> sources : sourcesSet) {
            ErrorFormatter.formatSources(sourceListIndex++, Lists.reverse(sources), formatter);
        }

    }

    public ScopeNotFoundError withSources(List<Object> newSources) {
        return new ScopeNotFoundError(this.scopeAnnotation, newSources);
    }
}
