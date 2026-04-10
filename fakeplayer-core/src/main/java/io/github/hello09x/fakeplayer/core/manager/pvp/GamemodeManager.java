package io.github.hello09x.fakeplayer.core.manager.pvp;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.config.GamemodeDefinition;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

@Singleton
public class GamemodeManager {

    private final FakeplayerConfig config;

    @Inject
    public GamemodeManager(FakeplayerConfig config) {
        this.config = config;
    }

    public GamemodeDefinition get(@NotNull String name) {
        return this.config.getGamemodes().get(name);
    }

    public void create(@NotNull String name) throws Exception {
        File file = this.getConfigFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        if (yml.getConfigurationSection("gamemodes") == null) {
            yml.createSection("gamemodes");
        }

        if (yml.getConfigurationSection("gamemodes." + name) != null) {
            throw new IllegalStateException("exists");
        }

        yml.set("gamemodes." + name + ".icon", Material.PAPER.name());
        yml.set("gamemodes." + name + ".display-color", NamedTextColor.YELLOW.toString());
        yml.set("gamemodes." + name + ".arenas", List.of());
        yml.save(file);
        this.config.reload();
    }

    public void delete(@NotNull String name) throws Exception {
        File file = this.getConfigFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        if (yml.getConfigurationSection("gamemodes." + name) == null) {
            throw new IllegalStateException("missing");
        }

        yml.set("gamemodes." + name, null);
        yml.set("queues." + name, null);
        yml.save(file);
        this.config.reload();
    }

    public void setIcon(@NotNull String name, @NotNull Material icon) throws Exception {
        File file = this.getConfigFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        if (yml.getConfigurationSection("gamemodes." + name) == null) {
            throw new IllegalStateException("missing");
        }

        yml.set("gamemodes." + name + ".icon", icon.name());
        yml.save(file);
        this.config.reload();
    }

    public void addArena(@NotNull String gamemode, @NotNull String arena) throws Exception {
        File file = this.getConfigFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String path = "gamemodes." + gamemode + ".arenas";

        if (yml.getConfigurationSection("gamemodes." + gamemode) == null) {
            throw new IllegalStateException("missing");
        }

        List<String> arenas = new ArrayList<>(yml.getStringList(path));
        if (!arenas.contains(arena)) {
            arenas.add(arena);
        }

        yml.set(path, arenas);
        yml.save(file);
        this.config.reload();
    }

    public void removeArena(@NotNull String gamemode, @NotNull String arena) throws Exception {
        File file = this.getConfigFile();
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        String path = "gamemodes." + gamemode + ".arenas";

        if (yml.getConfigurationSection("gamemodes." + gamemode) == null) {
            throw new IllegalStateException("missing");
        }

        List<String> arenas = new ArrayList<>(yml.getStringList(path));
        arenas.removeIf(arena::equalsIgnoreCase);
        yml.set(path, arenas);
        yml.save(file);
        this.config.reload();
    }

    private @NotNull File getConfigFile() {
        File folder = Main.getInstance().getDataFolder();
        if (!folder.exists()) {
            folder.mkdirs();
        }

        return new File(folder, "config.yml");
    }
}
