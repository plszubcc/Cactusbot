package io.github.hello09x.devtools.database.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.zaxxer.hikari.HikariConfig;
import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@Singleton
public class DatasourceConfig {

    public static final String CONFIG_SECTION = "datasource";
    private String driverClassName;
    private String url;
    private String username;
    private String password;
    private int maxPoolSize = 1;
    private long connectionTimeout = 1000L;
    private final Plugin plugin;

    @Inject
    public DatasourceConfig(@NotNull Plugin plugin) {
        super();
        this.plugin = plugin;
        File dataFolder = this.plugin.getDataFolder();

        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new IllegalStateException("Failed to create data folder: " + dataFolder.getAbsolutePath());
        } else {
            this.reload(plugin.getConfig());
        }
    }

    public void reload(@NotNull FileConfiguration configuration) {
        this.driverClassName = configuration.getString("datasource.driver-class", "org.sqlite.JDBC");
        File file = new File(this.plugin.getDataFolder(), "data.db");

        this.url = configuration.getString("datasource.url", "jdbc:sqlite:" + file.getAbsolutePath());
        this.username = configuration.getString("datasource.username");
        this.password = configuration.getString("datasource.password");
        this.maxPoolSize = configuration.getInt("datasource.max-pool-size", 1);
        this.connectionTimeout = (long) configuration.getInt("datasource.connection-timeout", 1000);
    }

    public @NotNull HikariConfig toHikariConfig() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(this.url);
        config.setUsername(this.username);
        config.setPassword(this.password);
        config.setPoolName(this.plugin.getName() + "-datasource");
        config.setMaximumPoolSize(this.maxPoolSize);
        config.setConnectionTimeout(this.connectionTimeout);
        return config;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public String getUrl() {
        return this.url;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public int getMaxPoolSize() {
        return this.maxPoolSize;
    }

    public long getConnectionTimeout() {
        return this.connectionTimeout;
    }

    public Plugin getPlugin() {
        return this.plugin;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public void setConnectionTimeout(long connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof DatasourceConfig)) {
            return false;
        } else {
            DatasourceConfig other = (DatasourceConfig) o;

            if (!other.canEqual(this)) {
                return false;
            } else if (this.getMaxPoolSize() != other.getMaxPoolSize()) {
                return false;
            } else if (this.getConnectionTimeout() != other.getConnectionTimeout()) {
                return false;
            } else {
                Object this$driverClassName = this.getDriverClassName();
                Object other$driverClassName = other.getDriverClassName();

                if (this$driverClassName == null) {
                    if (other$driverClassName != null) {
                        return false;
                    }
                } else if (!this$driverClassName.equals(other$driverClassName)) {
                    return false;
                }

                Object this$url = this.getUrl();
                Object other$url = other.getUrl();

                if (this$url == null) {
                    if (other$url != null) {
                        return false;
                    }
                } else if (!this$url.equals(other$url)) {
                    return false;
                }

                Object this$username = this.getUsername();
                Object other$username = other.getUsername();

                if (this$username == null) {
                    if (other$username != null) {
                        return false;
                    }
                } else if (!this$username.equals(other$username)) {
                    return false;
                }

                Object this$password = this.getPassword();
                Object other$password = other.getPassword();

                if (this$password == null) {
                    if (other$password != null) {
                        return false;
                    }
                } else if (!this$password.equals(other$password)) {
                    return false;
                }

                Object this$plugin = this.getPlugin();
                Object other$plugin = other.getPlugin();

                if (this$plugin == null) {
                    if (other$plugin != null) {
                        return false;
                    }
                } else if (!this$plugin.equals(other$plugin)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof DatasourceConfig;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;

        result = result * 59 + this.getMaxPoolSize();
        long $connectionTimeout = this.getConnectionTimeout();

        result = result * 59 + (int) ($connectionTimeout >>> 32 ^ $connectionTimeout);
        Object $driverClassName = this.getDriverClassName();

        result = result * 59 + ($driverClassName == null ? 43 : $driverClassName.hashCode());
        Object $url = this.getUrl();

        result = result * 59 + ($url == null ? 43 : $url.hashCode());
        Object $username = this.getUsername();

        result = result * 59 + ($username == null ? 43 : $username.hashCode());
        Object $password = this.getPassword();

        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        Object $plugin = this.getPlugin();

        result = result * 59 + ($plugin == null ? 43 : $plugin.hashCode());
        return result;
    }

    public String toString() {
        String s = this.getDriverClassName();

        return "DatasourceConfig(driverClassName=" + s + ", url=" + this.getUrl() + ", username=" + this.getUsername() + ", password=" + this.getPassword() + ", maxPoolSize=" + this.getMaxPoolSize() + ", connectionTimeout=" + this.getConnectionTimeout() + ", plugin=" + this.getPlugin() + ")";
    }
}
