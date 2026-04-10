package com.google.inject.spi;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.inject.internal.Messages;
import java.io.Serializable;
import java.util.Formatter;
import java.util.List;
import java.util.Optional;

public abstract class ErrorDetail<SelfT extends ErrorDetail<SelfT>> implements Serializable {

    private final String message;
    private final ImmutableList<Object> sources;
    private final Throwable cause;

    protected ErrorDetail(String message, List<Object> sources, Throwable cause) {
        super();
        this.message = message;
        this.sources = ImmutableList.copyOf(sources);
        this.cause = cause;
    }

    public boolean isMergeable(ErrorDetail<?> otherError) {
        return false;
    }

    public final void format(int index, List<ErrorDetail<?>> mergeableErrors, Formatter formatter) {
        String id = (String) this.getErrorIdentifier().map((sx) -> {
            return "[" + Messages.redBold(sx) + "]: ";
        }).orElse("");

        formatter.format("%s) %s%s\n", index, id, this.getMessage());
        this.formatDetail(mergeableErrors, formatter);
        Optional<String> learnMoreLink = this.getLearnMoreLink();

        if (learnMoreLink.isPresent()) {
            formatter.format("\n%s\n", Messages.bold("Learn more:"));
            formatter.format("  %s\n", Messages.underline((String) learnMoreLink.get()));
        }

    }

    protected abstract void formatDetail(List<ErrorDetail<?>> mergeableErrors, Formatter formatter);

    protected Optional<String> getLearnMoreLink() {
        return Optional.empty();
    }

    protected Optional<String> getErrorIdentifier() {
        return Optional.empty();
    }

    public String getMessage() {
        return this.message;
    }

    public List<Object> getSources() {
        return this.sources;
    }

    public Throwable getCause() {
        return this.cause;
    }

    public int hashCode() {
        return Objects.hashCode(new Object[]{this.message, this.cause, this.sources});
    }

    public boolean equals(Object o) {
        if (!(o instanceof ErrorDetail)) {
            return false;
        } else {
            ErrorDetail<?> e = (ErrorDetail) o;

            return this.message.equals(e.message) && Objects.equal(this.cause, e.cause) && this.sources.equals(e.sources);
        }
    }

    public abstract SelfT withSources(List<Object> newSources);
}
