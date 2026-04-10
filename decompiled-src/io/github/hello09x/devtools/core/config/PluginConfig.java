package io.github.hello09x.devtools.core.config;

import com.google.inject.Inject;
import io.github.hello09x.devtools.core.event.ConfigReloadedEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Objects;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.codehaus.plexus.util.IOUtil;
import org.jetbrains.annotations.NotNull;

public abstract class PluginConfig {

    public static final String CONFIG_FILE_NAME = "config.yml";
    public static final String CONFIG_TMPL_FILE_NAME = "config.tmpl.yml";
    private final Plugin plugin;

    protected PluginConfig(@NotNull Plugin plugin) {
        super();
        this.plugin = plugin;
    }

    public void reload() {
        this.doReload();
        Bukkit.getServer().getPluginManager().callEvent(new ConfigReloadedEvent(this.plugin));
    }

    @Inject
    protected void doReload() {
        File folder = this.plugin.getDataFolder();

        if (!folder.exists() && !folder.mkdirs()) {
            throw new IllegalStateException("Failed to create data folder for plugin: " + this.plugin.getName());
        } else {
            File tmplFile = new File(folder, "config.tmpl.yml");

            this.copyJarConfigFile(tmplFile);
            File configFile = new File(folder, "config.yml");

            if (!configFile.exists()) {
                this.copyJarConfigFile(configFile);
            }

            this.plugin.reloadConfig();
            this.reload(this.plugin.getConfig());
        }
    }

    public boolean isConfigFileExists() {
        return (new File(this.plugin.getDataFolder(), "config.yml")).exists();
    }

    public boolean copyJarConfigFile(@NotNull File file) {
        InputStream jarFile = this.plugin.getResource("config.yml");

        if (jarFile == null) {
            return false;
        } else {
            try (FileOutputStream out = new FileOutputStream(file)) {
                IOUtil.copy(jarFile, out);
                return true;
            } catch (IOException ioexception) {
                throw new UncheckedIOException("Failed to generate " + file.getAbsolutePath(), ioexception);
            }
        }
    }

    public boolean isConfigFileOutOfDate() {
        String version = (String) Optional.ofNullable(this.plugin.getConfig().getDefaults()).map((defx) -> {
            return defx.getString("version");
        }).orElse((Object) null);

        if (version == null) {
            return false;
        } else {
            String current = this.plugin.getConfig().getString("version");

            return !Objects.equals(current, version);
        }
    }

    protected abstract void reload(@NotNull FileConfiguration fileconfiguration);
}
