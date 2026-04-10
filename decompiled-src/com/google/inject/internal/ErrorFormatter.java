package com.google.inject.internal;

import java.util.Formatter;
import java.util.List;

final class ErrorFormatter {

    private ErrorFormatter() {
        super();
    }

    static void formatSources(int index, List<Object> sources, Formatter formatter) {
        for (int i = 0; i < sources.size(); ++i) {
            Object source = sources.get(i);

            if (i == 0) {
                formatter.format("%-3s: ", index);
            } else {
                formatter.format(SourceFormatter.INDENT);
            }

            (new SourceFormatter(source, formatter, i == 0)).format();
        }

    }

    static void formatSources(List<Object> sources, Formatter formatter) {
        for (int i = 0; i < sources.size(); ++i) {
            Object source = sources.get(i);

            formatter.format("  ");
            (new SourceFormatter(source, formatter, i == 0)).format();
        }

    }
}
