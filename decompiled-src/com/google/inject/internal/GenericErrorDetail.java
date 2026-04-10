package com.google.inject.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.inject.spi.ErrorDetail;
import java.io.Serializable;
import java.util.Formatter;
import java.util.List;

public final class GenericErrorDetail extends InternalErrorDetail<GenericErrorDetail> implements Serializable {

    public GenericErrorDetail(ErrorId errorId, String message, List<Object> sources, Throwable cause) {
        super(errorId, (String) Preconditions.checkNotNull(message, "message"), sources, cause);
    }

    public void formatDetail(List<ErrorDetail<?>> mergeableErrors, Formatter formatter) {
        Preconditions.checkArgument(mergeableErrors.isEmpty(), "Unexpected mergeable errors");
        List<Object> dependencies = this.getSources();

        for (Object source : Lists.reverse(dependencies)) {
            formatter.format("  ");
            (new SourceFormatter(source, formatter, false)).format();
        }

    }

    public GenericErrorDetail withSources(List<Object> newSources) {
        return new GenericErrorDetail(this.errorId, this.getMessage(), newSources, this.getCause());
    }
}
