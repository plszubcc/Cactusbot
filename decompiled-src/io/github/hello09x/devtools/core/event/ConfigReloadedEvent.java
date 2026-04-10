package io.github.hello09x.devtools.core.event;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class ConfigReloadedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final @NotNull Plugin plugin;
    private final FileConfiguration config;

    public @NotNull Plugin getPlugin() {
        return this.plugin;
    }

    public @NotNull FileConfiguration getConfig() {
        return this.config;
    }

    public ConfigReloadedEvent(@NotNull Plugin plugin) {
        super();
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public static @NotNull HandlerList getHandlerList() {
        return ConfigReloadedEvent.handlers;
    }

    public @NotNull HandlerList getHandlers() {
        return ConfigReloadedEvent.handlers;
    }
}
