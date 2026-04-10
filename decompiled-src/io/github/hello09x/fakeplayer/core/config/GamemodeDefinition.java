package io.github.hello09x.fakeplayer.core.config;

import java.util.List;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public record GamemodeDefinition(@NotNull String name, @NotNull Material icon, @NotNull NamedTextColor displayColor, @NotNull List<String> arenas) {

    public GamemodeDefinition {
        super();
    }
}
