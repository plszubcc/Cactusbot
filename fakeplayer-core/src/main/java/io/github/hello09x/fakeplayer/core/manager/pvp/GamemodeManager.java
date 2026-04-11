package io.github.hello09x.fakeplayer.core.manager.pvp;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.config.GamemodeDefinition;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class GamemodeManager {

    private final Map<String, GamemodeDefinition> gamemodes = new ConcurrentHashMap<>();
    private final Object fileLock = new Object();

    @Inject
    public GamemodeManager(FakeplayerConfig config) {
        this.gamemodes.putAll(config.getGamemodes());
    }

    public GamemodeDefinition get(@NotNull String name) {
        return this.gamemodes.get(name);
    }

    public @NotNull Map<String, GamemodeDefinition> all() {
        return Map.copyOf(this.gamemodes);
    }

    public @Nullable String findByArena(@NotNull String arena) {
        for (GamemodeDefinition definition : this.gamemodes.values()) {
            if (definition.arenas().stream().anyMatch(arena::equalsIgnoreCase)) {
                return definition.name();
            }
        }

        return null;
    }

    public void create(@NotNull String name) throws Exception {
        if (this.gamemodes.containsKey(name)) {
            throw new IllegalStateException("exists");
        }

        this.gamemodes.put(name, new GamemodeDefinition(name, Material.PAPER, NamedTextColor.YELLOW, List.of()));
        this.saveAsync(yml -> {
            if (yml.getConfigurationSection("gamemodes") == null) {
                yml.createSection("gamemodes");
            }
            yml.set("gamemodes." + name + ".icon", Material.PAPER.name());
            yml.set("gamemodes." + name + ".display-color", NamedTextColor.YELLOW.toString());
            yml.set("gamemodes." + name + ".arenas", List.of());
        });
    }

    public void delete(@NotNull String name) throws Exception {
        if (this.gamemodes.remove(name) == null) {
            throw new IllegalStateException("missing");
        }

        this.saveAsync(yml -> yml.set("gamemodes." + name, null));
    }

    public void setIcon(@NotNull String name, @NotNull Material icon) throws Exception {
        GamemodeDefinition current = this.gamemodes.get(name);
        if (current == null) {
            throw new IllegalStateException("missing");
        }

        this.gamemodes.put(name, new GamemodeDefinition(current.name(), icon, current.displayColor(), current.arenas()));
        this.saveAsync(yml -> yml.set("gamemodes." + name + ".icon", icon.name()));
    }

    public void addArena(@NotNull String gamemode, @NotNull String arena) throws Exception {
        GamemodeDefinition current = this.gamemodes.get(gamemode);
        if (current == null) {
            throw new IllegalStateException("missing");
        }

        List<String> arenas = new ArrayList<>(current.arenas());
        if (arenas.stream().noneMatch(arena::equalsIgnoreCase)) {
            arenas.add(arena);
        }

        this.gamemodes.put(gamemode, new GamemodeDefinition(current.name(), current.icon(), current.displayColor(), List.copyOf(arenas)));
        this.saveAsync(yml -> yml.set("gamemodes." + gamemode + ".arenas", arenas));
    }

    public void removeArena(@NotNull String gamemode, @NotNull String arena) throws Exception {
        GamemodeDefinition current = this.gamemodes.get(gamemode);
        if (current == null) {
            throw new IllegalStateException("missing");
        }

        List<String> arenas = new ArrayList<>(current.arenas());
        arenas.removeIf(arena::equalsIgnoreCase);
        this.gamemodes.put(gamemode, new GamemodeDefinition(current.name(), current.icon(), current.displayColor(), List.copyOf(arenas)));
        this.saveAsync(yml -> yml.set("gamemodes." + gamemode + ".arenas", arenas));
    }

    private @NotNull File getConfigFile() {
        File folder = Main.getInstance().getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return new File(folder, "config.yml");
    }

    private void saveAsync(@NotNull Consumer<YamlConfiguration> update) {
        File file = this.getConfigFile();
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            synchronized (this.fileLock) {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
                update.accept(yml);
                try {
                    yml.save(file);
                } catch (Exception ignored) {
                }
            }
        });
    }
}
