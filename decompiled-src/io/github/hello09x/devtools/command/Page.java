package io.github.hello09x.devtools.command;

import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public record Page<T>(int page, int size, int pages, int total, List<T> records) {

    public Page(int page, int size, int total, List<T> records) {
        this(page, size, (int) Math.ceil((double) total / (double) size), total, records);
    }

    public Page {
        super();
    }

    public static <U> Page<U> empty() {
        return new Page<U>(1, 0, 1, 0, Collections.emptyList());
    }

    public static <U> Page<U> of(List<U> data, int page, int size) {
        int total = data.size();

        if (total == 0) {
            return empty();
        } else {
            if (size < 1) {
                size = 10;
            }

            int pages = (int) Math.ceil((double) total / (double) size);

            page = Math.max(Math.min(page, pages), 1);
            int from = Math.min((page - 1) * size, total - 1);
            int to = Math.min(from + size, total);
            List<U> records = data.subList(from, to);

            return new Page<U>(page, size, pages, total, records);
        }
    }

    public boolean isEmpty() {
        return this.records.isEmpty();
    }

    public int size() {
        return this.records.size();
    }

    public boolean hasNext() {
        return this.page < this.pages;
    }

    public boolean hasPrevious() {
        return this.page > 1;
    }

    public @NotNull Component asComponent(@NotNull Component title, @NotNull Function<@UnknownNullability T, @NotNull Component> mapping, @NotNull Function<@NotNull Integer, @NotNull String> commandMapping) {
        Builder builder = Component.text();

        ((Builder) builder.append(title)).append(Component.newline());
        if (this.isEmpty()) {
            builder.append(Component.text("\n... Nothing here ...\n\n", NamedTextColor.GRAY));
        } else {
            ListIterator<T> itr = this.records.listIterator();

            while (itr.hasNext()) {
                int i = itr.nextIndex();
                T record = (T) itr.next();
                NamedTextColor color = i % 2 == 0 ? NamedTextColor.WHITE : NamedTextColor.GRAY;
                TextComponent row = (TextComponent) ((TextComponent) Component.text("", color).append((Component) mapping.apply(record))).append(Component.newline());

                builder.append(row);
            }
        }

        if (this.hasPrevious()) {
            builder.append(Component.text("◀").clickEvent(ClickEvent.runCommand((String) commandMapping.apply(this.page - 1))));
            builder.append(Component.text(" "));
        } else {
            builder.append(Component.text("◀ ", NamedTextColor.GRAY));
        }

        int left = this.page - 2;

        if (left <= 2) {
            for (int i = 1; i < this.page; ++i) {
                builder.append(Component.text(i).clickEvent(ClickEvent.runCommand((String) commandMapping.apply(i))));
                builder.append(Component.text(" "));
            }
        } else {
            builder.append(Component.text("1").clickEvent(ClickEvent.runCommand((String) commandMapping.apply(1))));
            builder.append(Component.text(" "));
            builder.append(Component.text("···").clickEvent(ClickEvent.runCommand((String) commandMapping.apply(Math.max(this.page - 5, 2)))));
            builder.append(Component.text(" "));

            for (int i = left; i < this.page; ++i) {
                builder.append(Component.text(i).clickEvent(ClickEvent.runCommand((String) commandMapping.apply(i))));
                builder.append(Component.text(" "));
            }
        }

        builder.append(Component.text(this.page, NamedTextColor.AQUA));
        builder.append(Component.text(" "));

        int next;

        for (next = this.page + 1; next <= this.pages && next <= this.page + 2; ++next) {
            builder.append(Component.text(next).clickEvent(ClickEvent.runCommand((String) commandMapping.apply(next))));
            builder.append(Component.text(" "));
        }

        if (next < this.pages) {
            builder.append(Component.text("···").clickEvent(ClickEvent.runCommand((String) commandMapping.apply(Math.min(this.page + 5, this.pages - 1)))));
            builder.append(Component.text(" "));
            builder.append(Component.text(this.pages).clickEvent(ClickEvent.runCommand((String) commandMapping.apply(this.pages))));
            builder.append(Component.text(" "));
        } else if (next == this.pages) {
            builder.append(Component.text(this.pages).clickEvent(ClickEvent.runCommand((String) commandMapping.apply(this.pages))));
            builder.append(Component.text(" "));
        }

        if (this.hasNext()) {
            builder.append(Component.text("▶").clickEvent(ClickEvent.runCommand((String) commandMapping.apply(this.page + 1))));
            builder.append(Component.text(" "));
        } else {
            builder.append(Component.text("▶ ", NamedTextColor.GRAY));
        }

        return builder.asComponent();
    }
}
