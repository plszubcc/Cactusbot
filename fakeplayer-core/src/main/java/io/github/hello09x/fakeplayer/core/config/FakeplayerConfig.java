package io.github.hello09x.fakeplayer.core.config;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.config.ConfigUtils;
import io.github.hello09x.devtools.core.config.PluginConfig;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.repository.model.Feature;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import lombok.Generated;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class FakeplayerConfig extends PluginConfig {

    private static final Logger log = Main.getInstance().getLogger();
    private static final String defaultNameChars = "^[a-zA-Z0-9_]+$";
    private int playerLimit;
    private int serverLimit;
    private String nameTemplate;
    private String namePrefix;
    private NamedTextColor nameStyleColor;
    private List<TextDecoration> nameStyleDecorations;
    private boolean followQuiting;
    private boolean followQuitingForce;
    private Integer followQuitingForceDelay;
    private boolean detectIp;
    private int kaleTps;
    private List<String> preSpawnCommands;
    private List<String> postSpawnCommands;
    private List<String> afterSpawnCommands;
    private List<String> postQuitCommands;
    private List<String> afterQuitCommands;
    private List<String> selfCommands;
    private boolean dropInventoryOnQuiting;
    private boolean persistData;
    private boolean kickOnDead;
    private Pattern namePattern;
    private boolean checkForUpdates;
    /** @deprecated */
    @Deprecated
    private Set<String> allowCommands;
    private @Nullable Duration lifespan;
    private boolean debug;
    private PreventKicking preventKicking;
    private InvseeImplement invseeImplement;
    private Map<Feature, String> defaultFeatures;
    private Map<String, PresetDefinition> presets = Map.of();
    private Map<String, CustomActionDefinition> actions = Map.of();
    private Map<String, ArenaDefinition> arenas = Map.of();
    private Map<String, PvpArenaDefinition> pvpArenas = Map.of();
    private Map<String, GamemodeDefinition> gamemodes = Map.of();
    private NamedTextColor matchTitleColor;
    private NamedTextColor matchSubtitleColor;
    private NamedTextColor countdownColor;
    private String matchTitleText;
    private String matchSubtitleText;
    private String fightText;
    private @Nullable Sound matchFoundSound;
    private float matchFoundSoundVolume;
    private float matchFoundSoundPitch;
    private @Nullable Sound countdownSound;
    private float countdownSoundVolume;
    private float countdownSoundPitch;
    private boolean silentCommands;
    private String matchWinMessage;
    private String matchLoseMessage;
    private String playGuiTitle;
    private @Nullable ArenaSpawn pvpLobby;
    private int pvpResultTeleportDelayTicks;
    private boolean arcadeSoundsEnabled;
    private float arcadeSoundsMasterVolume;
    private @Nullable Sound arcadeMatchFoundSound;
    private float arcadeMatchFoundPitch;
    private @Nullable Sound arcadeCountdownSound;
    private float arcadeCountdownPitch;
    private @Nullable Sound arcadeFightSound;
    private float arcadeFightPitch;
    private @Nullable Sound arcadeWinSound;
    private float arcadeWinPitch;
    private @Nullable Sound arcadeLoseSound;
    private float arcadeLosePitch;
    private boolean botSoundsEnabled;
    private List<BotSoundStep> botSpawnSounds = List.of();
    private List<BotSoundStep> botMoveSounds = List.of();
    private List<BotSoundStep> botHitSounds = List.of();
    private List<BotSoundStep> botDeathSounds = List.of();
    private int botMoveIntervalTicks;
    private double botMoveMinDistance;
    private float botSoundsMasterVolume;

    @Inject
    public FakeplayerConfig() {
        super(Main.getInstance());
    }

    private static int maxIfZero(int value) {
        return value <= 0 ? 2147483647 : value;
    }

    protected void reload(@NotNull FileConfiguration file) {
        this.syncMissingRuntimeConfig(file);
        this.playerLimit = maxIfZero(file.getInt("player-limit", 1));
        this.serverLimit = maxIfZero(file.getInt("server-limit", 1000));
        this.followQuiting = file.getBoolean("follow-quiting", true);
        this.followQuitingForce = file.getBoolean("follow-quiting-force", false);
        this.followQuitingForceDelay = file.getInt("follow-quiting-delay", 3);
        this.detectIp = file.getBoolean("detect-ip", false);
        this.kaleTps = file.getInt("kale-tps", 0);
        this.selfCommands = file.getStringList("self-commands");
        this.preSpawnCommands = file.getStringList("pre-spawn-commands");
        this.postSpawnCommands = file.getStringList("post-spawn-commands");
        this.afterSpawnCommands = file.getStringList("after-spawn-commands");
        this.postQuitCommands = file.getStringList("post-quit-commands");
        this.afterQuitCommands = file.getStringList("after-quit-commands");
        this.nameTemplate = file.getString("name-template", "");
        this.dropInventoryOnQuiting = file.getBoolean("drop-inventory-on-quiting", true);
        this.persistData = file.getBoolean("persist-data", true);
        this.kickOnDead = file.getBoolean("kick-on-dead", true);
        this.checkForUpdates = file.getBoolean("check-for-updates", true);
        this.namePattern = this.getNamePattern(file);
        this.preventKicking = this.getPreventKicking(file);
        this.nameTemplate = this.getNameTemplate(file);
        this.namePrefix = file.getString("name-prefix", "");
        this.lifespan = this.getLifespan(file);
        this.allowCommands = (Set) file.getStringList("allow-commands").stream().map((cx) -> {
            return cx.startsWith("/") ? cx.substring(1) : cx;
        }).filter((cx) -> {
            return !cx.isBlank();
        }).collect(Collectors.toSet());
        this.defaultFeatures = (Map) Arrays.stream(Feature.values()).collect(Collectors.toMap(Function.identity(), (keyx) -> {
            return file.getString("default-features." + keyx.name(), keyx.getDefaultOption());
        }));
        this.invseeImplement = (InvseeImplement) ConfigUtils.getEnum(file, "invsee-implement", InvseeImplement.class, InvseeImplement.AUTO);
        this.debug = file.getBoolean("debug", false);
        this.nameStyleColor = this.getNameStyleColor(file);
        this.nameStyleDecorations = this.getNameStyleDecorations(file);
        this.presets = this.loadPresets(file);
        this.actions = this.loadActions(file);
        this.arenas = this.loadArenas(file);
        this.pvpArenas = this.loadPvpArenas(file);
        this.gamemodes = this.loadGamemodes(file);
        this.matchTitleColor = this.getColor(file, "pvp.match-found.title-color", NamedTextColor.YELLOW);
        this.matchSubtitleColor = this.getColor(file, "pvp.match-found.subtitle-color", NamedTextColor.YELLOW);
        this.countdownColor = this.getColor(file, "pvp.match-found.countdown-color", NamedTextColor.YELLOW);
        this.matchTitleText = (String) Objects.requireNonNullElse(file.getString("pvp.match-found.title"), "Match Found!");
        this.matchSubtitleText = (String) Objects.requireNonNullElse(file.getString("pvp.match-found.subtitle"), "Starting in %d");
        this.fightText = (String) Objects.requireNonNullElse(file.getString("pvp.match-found.fight-text"), "Fight!");
        this.matchFoundSound = this.getSound(file, "pvp.match-found.sound", Sound.ENTITY_PLAYER_LEVELUP);
        this.matchFoundSoundVolume = (float) Math.max(0.0D, file.getDouble("pvp.match-found.sound-volume", 1.0D));
        this.matchFoundSoundPitch = (float) Math.max(0.0D, file.getDouble("pvp.match-found.sound-pitch", 1.0D));
        this.countdownSound = this.getSound(file, "pvp.match-found.countdown-sound", Sound.BLOCK_NOTE_BLOCK_PLING);
        this.countdownSoundVolume = (float) Math.max(0.0D, file.getDouble("pvp.match-found.countdown-sound-volume", 1.0D));
        this.countdownSoundPitch = (float) Math.max(0.0D, file.getDouble("pvp.match-found.countdown-sound-pitch", 1.0D));
        this.silentCommands = file.getBoolean("settings.silent-commands", true);
        this.matchWinMessage = (String) Objects.requireNonNullElse(file.getString("messages.win"), "&aYou win!");
        this.matchLoseMessage = (String) Objects.requireNonNullElse(file.getString("messages.lose"), "&cYou lose!");
        this.playGuiTitle = (String) Objects.requireNonNullElse(file.getString("gui.play.title"), "Bot PvP");
        this.pvpLobby = this.readSpawnCsv(file.getString("pvp.lobby"));
        this.pvpResultTeleportDelayTicks = Math.max(0, file.getInt("pvp.result-teleport-delay", 50));
        this.arcadeSoundsEnabled = file.getBoolean("pvp.arcade-sounds.enabled", true);
        this.arcadeSoundsMasterVolume = (float) Math.max(0.0D, file.getDouble("pvp.arcade-sounds.master-volume", 1.0D));
        this.arcadeMatchFoundSound = this.getSound(file, "pvp.arcade-sounds.match-found.sound", Sound.UI_TOAST_IN);
        this.arcadeMatchFoundPitch = (float) Math.max(0.0D, file.getDouble("pvp.arcade-sounds.match-found.pitch", 1.1D));
        this.arcadeCountdownSound = this.getSound(file, "pvp.arcade-sounds.countdown.sound", Sound.BLOCK_NOTE_BLOCK_PLING);
        this.arcadeCountdownPitch = (float) Math.max(0.0D, file.getDouble("pvp.arcade-sounds.countdown.pitch", 1.2D));
        this.arcadeFightSound = this.getSound(file, "pvp.arcade-sounds.fight.sound", Sound.ENTITY_PLAYER_LEVELUP);
        this.arcadeFightPitch = (float) Math.max(0.0D, file.getDouble("pvp.arcade-sounds.fight.pitch", 1.35D));
        this.arcadeWinSound = this.getSound(file, "pvp.arcade-sounds.win.sound", Sound.UI_TOAST_CHALLENGE_COMPLETE);
        this.arcadeWinPitch = (float) Math.max(0.0D, file.getDouble("pvp.arcade-sounds.win.pitch", 1.0D));
        this.arcadeLoseSound = this.getSound(file, "pvp.arcade-sounds.lose.sound", Sound.BLOCK_NOTE_BLOCK_BASS);
        this.arcadeLosePitch = (float) Math.max(0.0D, file.getDouble("pvp.arcade-sounds.lose.pitch", 0.8D));
        this.botSoundsEnabled = file.getBoolean("bot-sounds.enabled", true);
        this.botSpawnSounds = this.readBotSoundSteps(file, "bot-sounds.spawn");
        this.botMoveSounds = this.readBotSoundSteps(file, "bot-sounds.move.steps");
        this.botHitSounds = this.readBotSoundSteps(file, "bot-sounds.hit");
        this.botDeathSounds = this.readBotSoundSteps(file, "bot-sounds.death");
        this.botMoveIntervalTicks = Math.max(1, file.getInt("bot-sounds.move.interval", 8));
        this.botMoveMinDistance = Math.max(0.01D, file.getDouble("bot-sounds.move.min-distance", 0.18D));
        this.botSoundsMasterVolume = (float) Math.max(0.0D, file.getDouble("bot-sounds.master-volume", 1.0D));
        if (this.isConfigFileOutOfDate()) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (Main.getInstance().isEnabled()) {
                    Main.getInstance().getComponentLogger().warn(Component.translatable("fakeplayer.configuration.out-of-date"));
                }

            }, 1L);
        }

        if (!this.allowCommands.isEmpty()) {
            FakeplayerConfig.log.warning("allow-commands is deprecated which will be removed at 0.4.0, you should use Permissions Plugin to assign permission groups to fake players.");
        }

        List<String> preparingCommands = file.getStringList("preparing-commands");

        if (!preparingCommands.isEmpty()) {
            FakeplayerConfig.log.warning("preparing-commands is deprecated, use post-spawn-commands instead.");
            this.postSpawnCommands.addAll(preparingCommands);
        }

        List<String> destroyCommands = file.getStringList("destroy-commands");

        if (!destroyCommands.isEmpty()) {
            FakeplayerConfig.log.warning("destroy-commands is deprecated, use post-quit-commands instead.");
            this.postQuitCommands.addAll(destroyCommands);
        }

    }

    private @Nullable Duration getLifespan(@NotNull FileConfiguration file) {
        long minutes = file.getLong("lifespan");

        return minutes <= 0L ? null : Duration.ofMinutes(minutes);
    }

    private @NotNull Pattern getNamePattern(@NotNull FileConfiguration file) {
        try {
            return Pattern.compile(file.getString("name-pattern", "^[a-zA-Z0-9_]+$"));
        } catch (PatternSyntaxException patternsyntaxexception) {
            FakeplayerConfig.log.warning("Invalid name-pattern: " + file.getString("name-chars"));
            return Pattern.compile("^[a-zA-Z0-9_]+$");
        }
    }

    private @NotNull String getNameTemplate(@NotNull FileConfiguration file) {
        String tmpl = file.getString("name-template", "");

        if (!tmpl.startsWith("-") && !tmpl.startsWith("@")) {
            return tmpl;
        } else {
            FakeplayerConfig.log.warning("Invalid name template: " + this.nameTemplate);
            return "";
        }
    }

    private @NotNull PreventKicking getPreventKicking(@NotNull FileConfiguration file) {
        if (file.getBoolean("prevent-kicked-on-spawning", false)) {
            FakeplayerConfig.log.warning("prevent-kicked-on-spawning is deprecated which will be removed at 0.4.0, use prevent-kick instead");
            return PreventKicking.ON_SPAWNING;
        } else {
            return (PreventKicking) ConfigUtils.getEnum(file, "prevent-kicking", PreventKicking.class, PreventKicking.ON_SPAWNING);
        }
    }

    private @NotNull NamedTextColor getNameStyleColor(@NotNull FileConfiguration file) {
        String[] styles = ((String) Objects.requireNonNullElse(file.getString("name-style"), "")).split(",\\s*");
        NamedTextColor color = NamedTextColor.WHITE;

        for (String style : styles) {
            NamedTextColor c = (NamedTextColor) NamedTextColor.NAMES.value(style);

            if (c != null) {
                color = c;
            }
        }

        return color;
    }

    private @NotNull List<TextDecoration> getNameStyleDecorations(@NotNull FileConfiguration file) {
        String[] styles = ((String) Objects.requireNonNullElse(file.getString("name-style"), "")).split(",\\s*");
        ArrayList<TextDecoration> decorations = new ArrayList();

        for (String style : styles) {
            TextDecoration decoration = (TextDecoration) TextDecoration.NAMES.value(style);

            if (decoration != null) {
                decorations.add(decoration);
            }
        }

        return decorations;
    }

    private @NotNull Map<String, PresetDefinition> loadPresets(@NotNull FileConfiguration file) {
        ConfigurationSection section = file.getConfigurationSection("presets");

        if (section == null) {
            return Map.of();
        } else {
            HashMap<String, PresetDefinition> result = new HashMap();

            for (String presetName : section.getKeys(false)) {
                if (presetName != null && !presetName.isBlank()) {
                    List<PresetCommandEntry> commands = this.readCommandEntries(file.getMapList("presets." + presetName + ".commands"));
                    String inventory = file.getString("presets." + presetName + ".inventory");
                    Double reach = null;
                    String reachPath = "presets." + presetName + ".reach";

                    if (file.isSet(reachPath)) {
                        double v = file.getDouble(reachPath);

                        if (v > 0.0D) {
                            reach = v;
                        }
                    }

                    result.put(presetName, new PresetDefinition(List.copyOf(commands), inventory, reach));
                }
            }

            return Map.copyOf(result);
        }
    }

    private @NotNull Map<String, CustomActionDefinition> loadActions(@NotNull FileConfiguration file) {
        ConfigurationSection section = file.getConfigurationSection("actions");

        if (section == null) {
            return Map.of();
        } else {
            HashMap<String, CustomActionDefinition> result = new HashMap();

            for (String actionName : section.getKeys(false)) {
                if (actionName != null && !actionName.isBlank()) {
                    List<PresetCommandEntry> commands = this.readCommandEntries(file.getMapList("actions." + actionName + ".commands"));

                    result.put(actionName, new CustomActionDefinition(List.copyOf(commands)));
                }
            }

            return Map.copyOf(result);
        }
    }

    private @NotNull List<PresetCommandEntry> readCommandEntries(@NotNull List<Map<?, ?>> commandMaps) {
        ArrayList<PresetCommandEntry> commands = new ArrayList(commandMaps.size());

        for (Map<?, ?> map : commandMaps) {
            String cmd = Objects.toString(map.get("command"), "").trim();

            if (!cmd.isBlank()) {
                if (cmd.startsWith("/")) {
                    cmd = cmd.substring(1);
                }

                cmd = cmd.replace("%player%", "%p");
                int repeat = -1;
                Object repeatObj = map.get("repeat");

                if (repeatObj instanceof Number) {
                    Number n = (Number) repeatObj;

                    repeat = n.intValue();
                } else if (repeatObj != null) {
                    try {
                        repeat = Integer.parseInt(repeatObj.toString());
                    } catch (Exception exception) {
                        repeat = -1;
                    }
                }

                int delay = 0;
                Object delayObj = map.get("delay");

                if (delayObj instanceof Number) {
                    Number n = (Number) delayObj;

                    delay = n.intValue();
                } else if (delayObj != null) {
                    try {
                        delay = Integer.parseInt(delayObj.toString());
                    } catch (Exception exception1) {
                        delay = 0;
                    }
                }

                if (delay < 0) {
                    delay = 0;
                }

                int stop = -1;
                Object stopObj = map.get("stop");

                if (stopObj instanceof Number) {
                    Number n = (Number) stopObj;

                    stop = n.intValue();
                } else if (stopObj != null) {
                    try {
                        stop = Integer.parseInt(stopObj.toString());
                    } catch (Exception exception2) {
                        stop = -1;
                    }
                }

                int limit = -1;
                Object limitObj = map.get("limit");

                if (limitObj instanceof Number) {
                    Number n = (Number) limitObj;

                    limit = n.intValue();
                } else if (limitObj != null) {
                    try {
                        limit = Integer.parseInt(limitObj.toString());
                    } catch (Exception exception3) {
                        limit = -1;
                    }
                }

                PresetConditions conditions = this.readPresetConditions(map);
                String id = this.readString(map, "id");

                commands.add(new PresetCommandEntry(id, cmd, repeat, delay, stop, limit, conditions));
            }
        }

        return commands;
    }

    private @NotNull Map<String, ArenaDefinition> loadArenas(@NotNull FileConfiguration file) {
        ConfigurationSection section = file.getConfigurationSection("arenas");

        if (section == null) {
            return Map.of();
        } else {
            HashMap<String, ArenaDefinition> result = new HashMap();

            for (String arenaName : section.getKeys(false)) {
                if (arenaName != null && !arenaName.isBlank()) {
                    ArenaSpawn ps = this.readArenaSpawn(file, "arenas." + arenaName + ".player-spawn");
                    ArenaSpawn bs = this.readArenaSpawn(file, "arenas." + arenaName + ".bot-spawn");

                    if (ps != null && bs != null) {
                        String preset = file.getString("arenas." + arenaName + ".preset");

                        if (preset != null && preset.isBlank()) {
                            preset = null;
                        }

                        result.put(arenaName, new ArenaDefinition(ps, bs, preset));
                    }
                }
            }

            return Map.copyOf(result);
        }
    }

    private @Nullable ArenaSpawn readArenaSpawn(@NotNull FileConfiguration file, @NotNull String path) {
        String world = file.getString(path + ".world");

        if (world != null && !world.isBlank()) {
            double x = file.getDouble(path + ".x");
            double y = file.getDouble(path + ".y");
            double z = file.getDouble(path + ".z");
            float yaw = (float) file.getDouble(path + ".yaw", 0.0D);
            float pitch = (float) file.getDouble(path + ".pitch", 0.0D);

            return new ArenaSpawn(world, x, y, z, yaw, pitch);
        } else {
            return null;
        }
    }

    private @NotNull Map<String, PvpArenaDefinition> loadPvpArenas(@NotNull FileConfiguration file) {
        ConfigurationSection section = file.getConfigurationSection("arenas");

        if (section == null) {
            return Map.of();
        } else {
            HashMap<String, PvpArenaDefinition> result = new HashMap();

            for (String arenaName : section.getKeys(false)) {
                if (arenaName != null && !arenaName.isBlank()) {
                    String preset = file.getString("arenas." + arenaName + ".preset");

                    if (preset != null && preset.isBlank()) {
                        preset = null;
                    }

                    String bot = ((String) Objects.requireNonNullElse(file.getString("arenas." + arenaName + ".bot"), "")).trim();
                    String skin = file.getString("arenas." + arenaName + ".skin");

                    if (skin != null && skin.isBlank()) {
                        skin = null;
                    }

                    String playerInventory = file.getString("arenas." + arenaName + ".player-inventory");

                    if (playerInventory != null && playerInventory.isBlank()) {
                        playerInventory = null;
                    }

                    int rounds = Math.max(1, file.getInt("arenas." + arenaName + ".rounds", 1));
                    ArenaSpawn playerSpawn = this.readSpawnCsv(file.getString("arenas." + arenaName + ".spawn.player"));
                    ArenaSpawn botSpawn = this.readSpawnCsv(file.getString("arenas." + arenaName + ".spawn.bot"));

                    if (playerSpawn != null && botSpawn != null) {
                        boolean busy = file.getBoolean("arenas." + arenaName + ".busy", false);

                        result.put(arenaName, new PvpArenaDefinition(arenaName, preset, bot, skin, playerInventory, rounds, playerSpawn, botSpawn, busy));
                    }
                }
            }

            return Map.copyOf(result);
        }
    }

    private @Nullable ArenaSpawn readSpawnCsv(@Nullable String csv) {
        if (csv != null && !csv.isBlank()) {
            try {
                String[] parts = csv.split("\\s*,\\s*");

                if (parts.length < 4) {
                    return null;
                } else {
                    String world = parts[0];
                    double x = Double.parseDouble(parts[1]);
                    double y = Double.parseDouble(parts[2]);
                    double z = Double.parseDouble(parts[3]);
                    float yaw = parts.length >= 5 ? Float.parseFloat(parts[4]) : 0.0F;
                    float pitch = parts.length >= 6 ? Float.parseFloat(parts[5]) : 0.0F;

                    return new ArenaSpawn(world, x, y, z, yaw, pitch);
                }
            } catch (Exception exception) {
                return null;
            }
        } else {
            return null;
        }
    }

    private @NotNull Map<String, GamemodeDefinition> loadGamemodes(@NotNull FileConfiguration file) {
        ConfigurationSection section = file.getConfigurationSection("gamemodes");

        if (section == null) {
            return Map.of();
        } else {
            HashMap<String, GamemodeDefinition> result = new HashMap();

            for (String name : section.getKeys(false)) {
                if (name != null && !name.isBlank()) {
                    String iconName = (String) Objects.requireNonNullElse(file.getString("gamemodes." + name + ".icon"), "PAPER");
                    Material icon = Material.matchMaterial(iconName);

                    if (icon == null) {
                        icon = Material.PAPER;
                    }

                    List<String> arenas = file.getStringList("gamemodes." + name + ".arenas");
                    NamedTextColor color = this.getColor(file, "gamemodes." + name + ".display-color", NamedTextColor.YELLOW);

                    result.put(name, new GamemodeDefinition(name, icon, color, List.copyOf(arenas)));
                }
            }

            return Map.copyOf(result);
        }
    }

    private @NotNull NamedTextColor getColor(@NotNull FileConfiguration file, @NotNull String path, @NotNull NamedTextColor fallback) {
        String raw = ((String) Objects.requireNonNullElse(file.getString(path), "")).trim();

        if (raw.isBlank()) {
            return fallback;
        } else {
            NamedTextColor c = (NamedTextColor) NamedTextColor.NAMES.value(raw.toLowerCase());

            return c != null ? c : fallback;
        }
    }

    private @Nullable Sound getSound(@NotNull FileConfiguration file, @NotNull String path, @Nullable Sound fallback) {
        String raw = ((String) Objects.requireNonNullElse(file.getString(path), "")).trim();

        if (raw.isBlank()) {
            return fallback;
        } else if (raw.equalsIgnoreCase("none")) {
            return null;
        } else {
            try {
                return Sound.valueOf(raw.toUpperCase());
            } catch (Exception exception) {
                return fallback;
            }
        }
    }

    public @NotNull Component formatConfiguredMessage(@Nullable String template, @NotNull Map<String, String> placeholders) {
        String message = (String) Objects.requireNonNullElse(template, "");

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace((CharSequence) entry.getKey(), (CharSequence) entry.getValue());
        }

        return LegacyComponentSerializer.legacyAmpersand().deserialize(message);
    }

    private @Nullable PresetConditions readPresetConditions(@NotNull Map<?, ?> map) {
        Object object = map.get("if");
        Map map1;

        if (object instanceof Map<?, ?> nestedMap) {
            map1 = nestedMap;
        } else {
            map1 = Map.of();
        }

        Map<Object, Object> nested = map1;
        Double selfHealthMin = this.readDouble(map, nested, "self-health-min");
        Double selfHealthMax = this.readDouble(map, nested, "self-health-max");
        Boolean selfOnFire = this.readBoolean(map, nested, "self-on-fire");
        Double targetHealthMin = this.readDouble(map, nested, "target-health-min");
        Double targetHealthMax = this.readDouble(map, nested, "target-health-max");
        Double targetDistanceMin = this.readDouble(map, nested, "target-distance-min");
        Double targetDistanceMax = this.readDouble(map, nested, "target-distance-max");
        Boolean targetBlocking = this.readBoolean(map, nested, "target-blocking");
        Boolean targetHoldingAxe = this.readBoolean(map, nested, "target-holding-axe");
        Boolean targetInCobweb = this.readBoolean(map, nested, "target-in-cobweb");
        Boolean targetHit = this.readBoolean(map, nested, "target-hit");
        Integer targetHitWithinTicks = this.readInteger(map, nested, "target-hit-within");
        String afterCommand = this.readString(map, nested, "after-command");
        Integer afterCommandWithinTicks = this.readInteger(map, nested, "after-command-within");
        PresetConditions conditions = new PresetConditions(selfHealthMin, selfHealthMax, selfOnFire, targetHealthMin, targetHealthMax, targetDistanceMin, targetDistanceMax, targetBlocking, targetHoldingAxe, targetInCobweb, targetHit, targetHitWithinTicks, afterCommand, afterCommandWithinTicks);

        return conditions.isEmpty() ? null : conditions;
    }

    private @Nullable Double readDouble(@NotNull Map<?, ?> root, @NotNull Map<?, ?> nested, @NotNull String key) {
        Object value = nested.containsKey(key) ? nested.get(key) : root.get(key);

        if (value instanceof Number number) {
            return number.doubleValue();
        } else if (value == null) {
            return null;
        } else {
            try {
                return Double.parseDouble(value.toString());
            } catch (Exception exception) {
                return null;
            }
        }
    }

    private @Nullable Integer readInteger(@NotNull Map<?, ?> root, @NotNull Map<?, ?> nested, @NotNull String key) {
        Object value = nested.containsKey(key) ? nested.get(key) : root.get(key);

        if (value instanceof Number number) {
            return number.intValue();
        } else if (value == null) {
            return null;
        } else {
            try {
                return Integer.parseInt(value.toString().trim());
            } catch (Exception exception) {
                return null;
            }
        }
    }

    private @Nullable Boolean readBoolean(@NotNull Map<?, ?> root, @NotNull Map<?, ?> nested, @NotNull String key) {
        Object value = nested.containsKey(key) ? nested.get(key) : root.get(key);

        if (value instanceof Boolean bool) {
            return bool;
        } else if (value == null) {
            return null;
        } else {
            String text = value.toString().trim();

            return text.equalsIgnoreCase("true") ? true : (text.equalsIgnoreCase("false") ? false : null);
        }
    }

    private @Nullable String readString(@NotNull Map<?, ?> root, @NotNull Map<?, ?> nested, @NotNull String key) {
        Object value = nested.containsKey(key) ? nested.get(key) : root.get(key);

        if (value == null) {
            return null;
        } else {
            String text = value.toString().trim();

            return text.isEmpty() ? null : text;
        }
    }

    private @Nullable String readString(@NotNull Map<?, ?> root, @NotNull String key) {
        Object value = root.get(key);

        if (value == null) {
            return null;
        } else {
            String text = value.toString().trim();

            return text.isEmpty() ? null : text;
        }
    }

    private @NotNull List<BotSoundStep> readBotSoundSteps(@NotNull FileConfiguration file, @NotNull String path) {
        List<Map<?, ?>> maps = file.getMapList(path);

        if (maps.isEmpty()) {
            return List.of();
        } else {
            ArrayList<BotSoundStep> result = new ArrayList(maps.size());

            for (Map<?, ?> map : maps) {
                String soundName = Objects.toString(map.get("sound"), "").trim();

                if (!soundName.isBlank()) {
                    try {
                        Sound sound = Sound.valueOf(soundName.toUpperCase(Locale.ENGLISH));
                        Object object = map.get("volume");
                        float f;

                        if (object instanceof Number) {
                            Number n = (Number) object;

                            f = n.floatValue();
                        } else {
                            f = 1.0F;
                        }

                        float volume = f;
                        Object object1 = map.get("pitch");

                        if (object1 instanceof Number) {
                            Number n = (Number) object1;

                            f = n.floatValue();
                        } else {
                            f = 1.0F;
                        }

                        float pitch = f;
                        Object object2 = map.get("delay");
                        int i;

                        if (object2 instanceof Number) {
                            Number n = (Number) object2;

                            i = n.intValue();
                        } else {
                            i = 0;
                        }

                        int delay = i;

                        result.add(new BotSoundStep(sound, Math.max(0.0F, volume), Math.max(0.1F, pitch), Math.max(0, delay)));
                    } catch (Exception exception) {
                        ;
                    }
                }
            }

            return List.copyOf(result);
        }
    }

    private void syncMissingRuntimeConfig(@NotNull FileConfiguration file) {
        boolean changed = false;

        changed |= this.ensure(file, "settings.silent-commands", true);
        changed |= this.ensure(file, "messages.win", "&aYou win!");
        changed |= this.ensure(file, "messages.lose", "&cYou lose!");
        changed |= this.ensure(file, "gui.play.title", "Bot PvP");
        changed |= this.ensure(file, "pvp.lobby", "");
        changed |= this.ensure(file, "pvp.result-teleport-delay", 50);
        changed |= this.ensure(file, "pvp.arcade-sounds.enabled", true);
        changed |= this.ensure(file, "pvp.arcade-sounds.master-volume", 1.0D);
        changed |= this.ensure(file, "pvp.arcade-sounds.match-found.sound", "UI_TOAST_IN");
        changed |= this.ensure(file, "pvp.arcade-sounds.match-found.pitch", 1.1D);
        changed |= this.ensure(file, "pvp.arcade-sounds.countdown.sound", "BLOCK_NOTE_BLOCK_PLING");
        changed |= this.ensure(file, "pvp.arcade-sounds.countdown.pitch", 1.2D);
        changed |= this.ensure(file, "pvp.arcade-sounds.fight.sound", "ENTITY_PLAYER_LEVELUP");
        changed |= this.ensure(file, "pvp.arcade-sounds.fight.pitch", 1.35D);
        changed |= this.ensure(file, "pvp.arcade-sounds.win.sound", "UI_TOAST_CHALLENGE_COMPLETE");
        changed |= this.ensure(file, "pvp.arcade-sounds.win.pitch", 1.0D);
        changed |= this.ensure(file, "pvp.arcade-sounds.lose.sound", "BLOCK_NOTE_BLOCK_BASS");
        changed |= this.ensure(file, "pvp.arcade-sounds.lose.pitch", 0.8D);
        changed |= this.ensure(file, "bot-sounds.enabled", true);
        changed |= this.ensure(file, "bot-sounds.master-volume", 1.0D);
        changed |= this.ensure(file, "bot-sounds.move.interval", 8);
        changed |= this.ensure(file, "bot-sounds.move.min-distance", 0.18D);
        changed |= this.ensure(file, "presets.mace-ordinary.commands", this.buildMaceOrdinaryPreset());
        changed |= this.ensure(file, "presets.mace-ordinary.inventory", "");
        changed |= this.ensure(file, "presets.mace-ordinary.reach", 3.0D);
        changed |= this.ensure(file, "presets.mace-elytra.commands", this.buildMaceElytraPreset());
        changed |= this.ensure(file, "presets.mace-elytra.inventory", "");
        changed |= this.ensure(file, "presets.mace-elytra.reach", 3.0D);
        changed |= this.ensure(file, "presets.uhc.commands", this.buildUhcPreset());
        changed |= this.ensure(file, "presets.uhc.inventory", "");
        changed |= this.ensure(file, "presets.uhc.reach", 3.0D);
        changed |= this.ensure(file, "actions.crystal-fast.commands", this.buildCrystalFastAction());
        ConfigurationSection gamemodeSection = file.getConfigurationSection("gamemodes");

        if (gamemodeSection != null) {
            for (String name : gamemodeSection.getKeys(false)) {
                changed |= this.ensure(file, "gamemodes." + name + ".display-color", "yellow");
            }
        }

        if (changed) {
            try {
                if (file instanceof YamlConfiguration) {
                    YamlConfiguration yaml = (YamlConfiguration) file;

                    yaml.save(this.getConfigFile());
                } else {
                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(this.getConfigFile());

                    yaml.set("settings.silent-commands", file.get("settings.silent-commands"));
                    yaml.set("messages.win", file.get("messages.win"));
                    yaml.set("messages.lose", file.get("messages.lose"));
                    yaml.set("gui.play.title", file.get("gui.play.title"));
                    yaml.set("pvp.lobby", file.get("pvp.lobby"));
                    yaml.set("pvp.result-teleport-delay", file.get("pvp.result-teleport-delay"));
                    yaml.set("pvp.arcade-sounds.enabled", file.get("pvp.arcade-sounds.enabled"));
                    yaml.set("pvp.arcade-sounds.master-volume", file.get("pvp.arcade-sounds.master-volume"));
                    yaml.set("pvp.arcade-sounds.match-found.sound", file.get("pvp.arcade-sounds.match-found.sound"));
                    yaml.set("pvp.arcade-sounds.match-found.pitch", file.get("pvp.arcade-sounds.match-found.pitch"));
                    yaml.set("pvp.arcade-sounds.countdown.sound", file.get("pvp.arcade-sounds.countdown.sound"));
                    yaml.set("pvp.arcade-sounds.countdown.pitch", file.get("pvp.arcade-sounds.countdown.pitch"));
                    yaml.set("pvp.arcade-sounds.fight.sound", file.get("pvp.arcade-sounds.fight.sound"));
                    yaml.set("pvp.arcade-sounds.fight.pitch", file.get("pvp.arcade-sounds.fight.pitch"));
                    yaml.set("pvp.arcade-sounds.win.sound", file.get("pvp.arcade-sounds.win.sound"));
                    yaml.set("pvp.arcade-sounds.win.pitch", file.get("pvp.arcade-sounds.win.pitch"));
                    yaml.set("pvp.arcade-sounds.lose.sound", file.get("pvp.arcade-sounds.lose.sound"));
                    yaml.set("pvp.arcade-sounds.lose.pitch", file.get("pvp.arcade-sounds.lose.pitch"));
                    yaml.set("bot-sounds.enabled", file.get("bot-sounds.enabled"));
                    yaml.set("bot-sounds.master-volume", file.get("bot-sounds.master-volume"));
                    yaml.set("bot-sounds.move.interval", file.get("bot-sounds.move.interval"));
                    yaml.set("bot-sounds.move.min-distance", file.get("bot-sounds.move.min-distance"));
                    yaml.set("presets.mace-ordinary.commands", file.get("presets.mace-ordinary.commands"));
                    yaml.set("presets.mace-ordinary.inventory", file.get("presets.mace-ordinary.inventory"));
                    yaml.set("presets.mace-ordinary.reach", file.get("presets.mace-ordinary.reach"));
                    yaml.set("presets.mace-elytra.commands", file.get("presets.mace-elytra.commands"));
                    yaml.set("presets.mace-elytra.inventory", file.get("presets.mace-elytra.inventory"));
                    yaml.set("presets.mace-elytra.reach", file.get("presets.mace-elytra.reach"));
                    yaml.set("presets.uhc.commands", file.get("presets.uhc.commands"));
                    yaml.set("presets.uhc.inventory", file.get("presets.uhc.inventory"));
                    yaml.set("presets.uhc.reach", file.get("presets.uhc.reach"));
                    yaml.set("actions.crystal-fast.commands", file.get("actions.crystal-fast.commands"));
                    if (gamemodeSection != null) {
                        for (String name : gamemodeSection.getKeys(false)) {
                            yaml.set("gamemodes." + name + ".display-color", file.get("gamemodes." + name + ".display-color"));
                        }
                    }

                    yaml.save(this.getConfigFile());
                }
            } catch (Exception exception) {
                FakeplayerConfig.log.warning("Failed to sync new config keys: " + exception.getMessage());
            }

        }
    }

    private boolean ensure(@NotNull FileConfiguration file, @NotNull String path, @NotNull Object value) {
        if (file.isSet(path)) {
            return false;
        } else {
            file.set(path, value);
            return true;
        }
    }

    private @NotNull List<Map<String, Object>> buildMaceOrdinaryPreset() {
        return List.of(this.commandEntry("cb look target %target% %p", 10, 0, (Integer) null, (Map) null), this.commandEntry("cb move forward %p", 6, 0, (Integer) null, (Map) null), this.commandEntry("cb mace %p", -1, 0, (Integer) null, (Map) null), this.commandEntry("cb sprint %p on", 20, 0, 120, (Map) null), this.commandEntry("cb hold 7 %p", 40, 0, (Integer) null, (Map) null), this.commandEntry("cb look down %p", 40, 0, (Integer) null, this.distanceMax(8.0D)), this.commandEntry("cb use once %p", 40, 1, (Integer) null, this.distanceMax(8.0D)), this.commandEntry("cb jump once %p", 40, 2, (Integer) null, this.distanceMax(8.0D)), this.commandEntry("cb hold 5 %p", 40, 8, (Integer) null, (Map) null), this.commandEntry("cb attack once %p", 30, 8, (Integer) null, this.distanceMax(8.0D)));
    }

    private @NotNull List<Map<String, Object>> buildMaceElytraPreset() {
        return List.of(this.commandEntry("cb move forward %p", 6, 0, (Integer) null, (Map) null), this.commandEntry("cb mace %p", -1, 0, (Integer) null, (Map) null), this.commandEntry("cb sprint %p on", 20, 0, 120, (Map) null), this.commandEntry("cb hold 7 %p", 50, 0, (Integer) null, (Map) null), this.commandEntry("cb turn back %p", 50, 0, (Integer) null, this.distanceMax(12.0D)), this.commandEntry("cb look down %p", 50, 1, (Integer) null, this.distanceMax(12.0D)), this.commandEntry("cb jump once %p", 50, 2, (Integer) null, this.distanceMax(12.0D)), this.commandEntry("cb glide %p", 50, 3, (Integer) null, this.distanceMax(12.0D)), this.commandEntry("cb use once %p", 50, 4, (Integer) null, this.distanceMax(12.0D)), this.commandEntry("cb look up %p", 50, 6, (Integer) null, (Map) null), this.commandEntry("cb hold 5 %p", 50, 10, (Integer) null, (Map) null), this.commandEntry("cb look target %target% %p", 50, 11, (Integer) null, (Map) null), this.commandEntry("cb look target %target% %p", 8, 14, (Integer) null, (Map) null), this.commandEntry("cb attack once %p", 50, 15, (Integer) null, this.distanceMax(4.5D)));
    }

    private @NotNull List<Map<String, Object>> buildCrystalFastAction() {
        return List.of(this.commandEntry("cb hold netherite_sword %p", 20, 0, (Integer) null, this.distanceMax(3.0D)), this.commandEntryWithId("sprinthit", "cb attack once %p", 20, 1, (Integer) null, (Integer) null, this.distanceMax(3.0D)), this.commandEntry("cb hold obsidian %p", 20, 16, (Integer) null, this.crystalFastStartConditions(10)), this.commandEntryWithId("obs_place", "cb place %p 3.0", 20, 18, (Integer) null, (Integer) null, this.crystalFastStartConditions(20)), this.commandEntry("cb hold end_crystal %p", 20, 19, (Integer) null, this.afterCommand("obs_place", 10)), this.commandEntry("cb crystal place %p 4.0", 20, 20, (Integer) null, this.afterCommand("obs_place", 10)), this.commandEntry("cb crystal break %p 4.0", 20, 32, (Integer) null, this.afterCommand("obs_place", 20)));
    }

    private @NotNull List<Map<String, Object>> buildUhcPreset() {
        return List.of(this.commandEntry("cb mace %p off", -1, 0, (Integer) null, (Map) null), this.commandEntry("cb sprint %p on", 20, 0, (Integer) null, (Map) null), this.commandEntry("cb look target %target% %p", 4, 0, (Integer) null, (Map) null), this.commandEntry("cb move forward %p", 8, 0, (Integer) null, (Map) null), this.commandEntry("cb move left %p", 16, 0, (Integer) null, (Map) null), this.commandEntry("cb move right %p", 16, 8, (Integer) null, (Map) null), this.commandEntry("cb hold netherite_sword %p", 20, 0, (Integer) null, (Map) null), this.commandEntry("cb attack once %p", 12, 1, (Integer) null, this.distanceMax(3.0D)), this.commandEntry("cb sprint %p off", 26, 8, (Integer) null, this.distanceMax(3.2D)), this.commandEntry("cb crit %p", 26, 9, (Integer) null, this.distanceMax(3.2D)), this.commandEntry("cb jump once %p", 26, 10, (Integer) null, this.distanceMax(3.2D)), this.commandEntry("cb attack once %p", 26, 12, (Integer) null, this.distanceMax(3.2D)), this.commandEntry("cb sprint %p on", 26, 15, (Integer) null, (Map) null), this.commandEntry("cb hold golden_apple %p", 40, 0, (Integer) null, this.healthMax(12.0D)), this.commandEntry("cb use once %p", 40, 2, (Integer) null, this.healthMax(12.0D)), this.commandEntry("cb hold player_head %p", 50, 5, (Integer) null, this.healthMax(8.0D)), this.commandEntry("cb use once %p", 50, 7, (Integer) null, this.healthMax(8.0D)), this.commandEntry("cb hold water_bucket %p", 20, 0, (Integer) null, this.selfOnFire()), this.commandEntry("cb look down %p", 20, 1, (Integer) null, this.selfOnFire()), this.commandEntry("cb use once %p", 20, 2, (Integer) null, this.selfOnFire()), this.commandEntry("cb hold bucket %p", 20, 4, (Integer) null, this.selfOnFire()), this.commandEntry("cb bucket pickup %p 3.0", 20, 5, (Integer) null, this.selfOnFire()), this.commandEntry("cb hold cobweb %p", 28, 14, (Integer) null, this.closeTarget(3.5D)), this.commandEntry("cb place %p 3.0 right", 28, 15, (Integer) null, this.closeTarget(3.5D)), this.commandEntry("cb hold lava_bucket %p", 28, 17, (Integer) null, this.inCobwebTarget()), this.commandEntry("cb lava above %p 4.0", 28, 18, (Integer) null, this.inCobwebTarget()), this.commandEntry("cb hold bucket %p", 28, 20, (Integer) null, this.inCobwebTarget()), this.commandEntry("cb bucket pickup %p 3.0", 28, 21, (Integer) null, this.inCobwebTarget()), this.commandEntry("cb hold shield %p", 24, 18, (Integer) null, this.shieldDefendConditions()), this.commandEntry("cb use once %p", 24, 19, (Integer) null, this.shieldDefendConditions()), this.commandEntry("cb hold netherite_sword %p", 24, 22, (Integer) null, this.attackAxeTargetConditions()));
    }

    private @NotNull Map<String, Object> commandEntry(@NotNull String command, int repeat, int delay, @Nullable Integer stop, @Nullable Map<String, Object> conditions) {
        LinkedHashMap<String, Object> map = new LinkedHashMap();

        map.put("command", command);
        map.put("repeat", repeat);
        if (delay > 0) {
            map.put("delay", delay);
        }

        if (stop != null && stop > 0) {
            map.put("stop", stop);
        }

        if (conditions != null && !conditions.isEmpty()) {
            map.put("if", conditions);
        }

        return map;
    }

    private @NotNull Map<String, Object> commandEntryWithId(@NotNull String id, @NotNull String command, int repeat, int delay, @Nullable Integer stop, @Nullable Integer limit, @Nullable Map<String, Object> conditions) {
        LinkedHashMap<String, Object> map = new LinkedHashMap();

        map.put("id", id);
        map.put("command", command);
        map.put("repeat", repeat);
        if (delay > 0) {
            map.put("delay", delay);
        }

        if (stop != null && stop > 0) {
            map.put("stop", stop);
        }

        if (limit != null && limit > 0) {
            map.put("limit", limit);
        }

        if (conditions != null && !conditions.isEmpty()) {
            map.put("if", conditions);
        }

        return map;
    }

    private @NotNull Map<String, Object> distanceMax(double value) {
        LinkedHashMap<String, Object> map = new LinkedHashMap();

        map.put("target-distance-max", value);
        return map;
    }

    private @NotNull Map<String, Object> healthMax(double value) {
        LinkedHashMap<String, Object> map = new LinkedHashMap();

        map.put("self-health-max", value);
        return map;
    }

    private @NotNull Map<String, Object> selfOnFire() {
        LinkedHashMap<String, Object> map = new LinkedHashMap();

        map.put("self-on-fire", true);
        return map;
    }

    private @NotNull Map<String, Object> closeTarget(double value) {
        LinkedHashMap<String, Object> map = new LinkedHashMap();

        map.put("target-distance-max", value);
        return map;
    }

    private @NotNull Map<String, Object> inCobwebTarget() {
        LinkedHashMap<String, Object> map = new LinkedHashMap();

        map.put("target-in-cobweb", true);
        return map;
    }

    private @NotNull Map<String, Object> shieldDefendConditions() {
        LinkedHashMap<String, Object> map = new LinkedHashMap();

        map.put("target-distance-min", 3.0D);
        map.put("target-holding-axe", false);
        return map;
    }

    private @NotNull Map<String, Object> attackAxeTargetConditions() {
        LinkedHashMap<String, Object> map = new LinkedHashMap();

        map.put("target-holding-axe", true);
        map.put("target-distance-max", 3.5D);
        return map;
    }

    private @NotNull Map<String, Object> crystalFastStartConditions(int targetHitWithinTicks) {
        LinkedHashMap<String, Object> map = new LinkedHashMap();

        map.put("after-command", "sprinthit");
        map.put("target-hit", true);
        map.put("target-hit-within", targetHitWithinTicks);
        return map;
    }

    private @NotNull Map<String, Object> afterCommand(@NotNull String id, int withinTicks) {
        LinkedHashMap<String, Object> map = new LinkedHashMap();

        map.put("after-command", id);
        map.put("after-command-within", withinTicks);
        return map;
    }

    private @NotNull File getConfigFile() {
        Main plugin = Main.getInstance();
        File folder = plugin.getDataFolder();

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return new File(folder, "config.yml");
    }

    @Generated
    public int getPlayerLimit() {
        return this.playerLimit;
    }

    @Generated
    public int getServerLimit() {
        return this.serverLimit;
    }

    @Generated
    public String getNameTemplate() {
        return this.nameTemplate;
    }

    @Generated
    public String getNamePrefix() {
        return this.namePrefix;
    }

    @Generated
    public NamedTextColor getNameStyleColor() {
        return this.nameStyleColor;
    }

    @Generated
    public List<TextDecoration> getNameStyleDecorations() {
        return this.nameStyleDecorations;
    }

    @Generated
    public boolean isFollowQuiting() {
        return this.followQuiting;
    }

    @Generated
    public boolean isFollowQuitingForce() {
        return this.followQuitingForce;
    }

    @Generated
    public Integer getFollowQuitingForceDelay() {
        return this.followQuitingForceDelay;
    }

    @Generated
    public boolean isDetectIp() {
        return this.detectIp;
    }

    @Generated
    public int getKaleTps() {
        return this.kaleTps;
    }

    @Generated
    public List<String> getPreSpawnCommands() {
        return this.preSpawnCommands;
    }

    @Generated
    public List<String> getPostSpawnCommands() {
        return this.postSpawnCommands;
    }

    @Generated
    public List<String> getAfterSpawnCommands() {
        return this.afterSpawnCommands;
    }

    @Generated
    public List<String> getPostQuitCommands() {
        return this.postQuitCommands;
    }

    @Generated
    public List<String> getAfterQuitCommands() {
        return this.afterQuitCommands;
    }

    @Generated
    public List<String> getSelfCommands() {
        return this.selfCommands;
    }

    @Generated
    public boolean isDropInventoryOnQuiting() {
        return this.dropInventoryOnQuiting;
    }

    @Generated
    public boolean isPersistData() {
        return this.persistData;
    }

    @Generated
    public boolean isKickOnDead() {
        return this.kickOnDead;
    }

    @Generated
    public Pattern getNamePattern() {
        return this.namePattern;
    }

    @Generated
    public boolean isCheckForUpdates() {
        return this.checkForUpdates;
    }

    /** @deprecated */
    @Deprecated
    @Generated
    public Set<String> getAllowCommands() {
        return this.allowCommands;
    }

    @Generated
    public @Nullable Duration getLifespan() {
        return this.lifespan;
    }

    @Generated
    public boolean isDebug() {
        return this.debug;
    }

    @Generated
    public PreventKicking getPreventKicking() {
        return this.preventKicking;
    }

    @Generated
    public InvseeImplement getInvseeImplement() {
        return this.invseeImplement;
    }

    @Generated
    public Map<Feature, String> getDefaultFeatures() {
        return this.defaultFeatures;
    }

    @Generated
    public Map<String, PresetDefinition> getPresets() {
        return this.presets;
    }

    @Generated
    public Map<String, CustomActionDefinition> getActions() {
        return this.actions;
    }

    @Generated
    public Map<String, ArenaDefinition> getArenas() {
        return this.arenas;
    }

    @Generated
    public Map<String, PvpArenaDefinition> getPvpArenas() {
        return this.pvpArenas;
    }

    @Generated
    public Map<String, GamemodeDefinition> getGamemodes() {
        return this.gamemodes;
    }

    @Generated
    public NamedTextColor getMatchTitleColor() {
        return this.matchTitleColor;
    }

    @Generated
    public NamedTextColor getMatchSubtitleColor() {
        return this.matchSubtitleColor;
    }

    @Generated
    public NamedTextColor getCountdownColor() {
        return this.countdownColor;
    }

    @Generated
    public String getMatchTitleText() {
        return this.matchTitleText;
    }

    @Generated
    public String getMatchSubtitleText() {
        return this.matchSubtitleText;
    }

    @Generated
    public String getFightText() {
        return this.fightText;
    }

    @Generated
    public @Nullable Sound getMatchFoundSound() {
        return this.matchFoundSound;
    }

    @Generated
    public float getMatchFoundSoundVolume() {
        return this.matchFoundSoundVolume;
    }

    @Generated
    public float getMatchFoundSoundPitch() {
        return this.matchFoundSoundPitch;
    }

    @Generated
    public @Nullable Sound getCountdownSound() {
        return this.countdownSound;
    }

    @Generated
    public float getCountdownSoundVolume() {
        return this.countdownSoundVolume;
    }

    @Generated
    public float getCountdownSoundPitch() {
        return this.countdownSoundPitch;
    }

    @Generated
    public boolean isSilentCommands() {
        return this.silentCommands;
    }

    @Generated
    public String getMatchWinMessage() {
        return this.matchWinMessage;
    }

    @Generated
    public String getMatchLoseMessage() {
        return this.matchLoseMessage;
    }

    @Generated
    public String getPlayGuiTitle() {
        return this.playGuiTitle;
    }

    @Generated
    public @Nullable ArenaSpawn getPvpLobby() {
        return this.pvpLobby;
    }

    @Generated
    public int getPvpResultTeleportDelayTicks() {
        return this.pvpResultTeleportDelayTicks;
    }

    @Generated
    public boolean isArcadeSoundsEnabled() {
        return this.arcadeSoundsEnabled;
    }

    @Generated
    public float getArcadeSoundsMasterVolume() {
        return this.arcadeSoundsMasterVolume;
    }

    @Generated
    public @Nullable Sound getArcadeMatchFoundSound() {
        return this.arcadeMatchFoundSound;
    }

    @Generated
    public float getArcadeMatchFoundPitch() {
        return this.arcadeMatchFoundPitch;
    }

    @Generated
    public @Nullable Sound getArcadeCountdownSound() {
        return this.arcadeCountdownSound;
    }

    @Generated
    public float getArcadeCountdownPitch() {
        return this.arcadeCountdownPitch;
    }

    @Generated
    public @Nullable Sound getArcadeFightSound() {
        return this.arcadeFightSound;
    }

    @Generated
    public float getArcadeFightPitch() {
        return this.arcadeFightPitch;
    }

    @Generated
    public @Nullable Sound getArcadeWinSound() {
        return this.arcadeWinSound;
    }

    @Generated
    public float getArcadeWinPitch() {
        return this.arcadeWinPitch;
    }

    @Generated
    public @Nullable Sound getArcadeLoseSound() {
        return this.arcadeLoseSound;
    }

    @Generated
    public float getArcadeLosePitch() {
        return this.arcadeLosePitch;
    }

    @Generated
    public boolean isBotSoundsEnabled() {
        return this.botSoundsEnabled;
    }

    @Generated
    public List<BotSoundStep> getBotSpawnSounds() {
        return this.botSpawnSounds;
    }

    @Generated
    public List<BotSoundStep> getBotMoveSounds() {
        return this.botMoveSounds;
    }

    @Generated
    public List<BotSoundStep> getBotHitSounds() {
        return this.botHitSounds;
    }

    @Generated
    public List<BotSoundStep> getBotDeathSounds() {
        return this.botDeathSounds;
    }

    @Generated
    public int getBotMoveIntervalTicks() {
        return this.botMoveIntervalTicks;
    }

    @Generated
    public double getBotMoveMinDistance() {
        return this.botMoveMinDistance;
    }

    @Generated
    public float getBotSoundsMasterVolume() {
        return this.botSoundsMasterVolume;
    }

    @Generated
    public String toString() {
        int i = this.getPlayerLimit();

        return "FakeplayerConfig(playerLimit=" + i + ", serverLimit=" + this.getServerLimit() + ", nameTemplate=" + this.getNameTemplate() + ", namePrefix=" + this.getNamePrefix() + ", nameStyleColor=" + String.valueOf(this.getNameStyleColor()) + ", nameStyleDecorations=" + String.valueOf(this.getNameStyleDecorations()) + ", followQuiting=" + this.isFollowQuiting() + ", followQuitingForce=" + this.isFollowQuitingForce() + ", followQuitingForceDelay=" + this.getFollowQuitingForceDelay() + ", detectIp=" + this.isDetectIp() + ", kaleTps=" + this.getKaleTps() + ", preSpawnCommands=" + String.valueOf(this.getPreSpawnCommands()) + ", postSpawnCommands=" + String.valueOf(this.getPostSpawnCommands()) + ", afterSpawnCommands=" + String.valueOf(this.getAfterSpawnCommands()) + ", postQuitCommands=" + String.valueOf(this.getPostQuitCommands()) + ", afterQuitCommands=" + String.valueOf(this.getAfterQuitCommands()) + ", selfCommands=" + String.valueOf(this.getSelfCommands()) + ", dropInventoryOnQuiting=" + this.isDropInventoryOnQuiting() + ", persistData=" + this.isPersistData() + ", kickOnDead=" + this.isKickOnDead() + ", namePattern=" + String.valueOf(this.getNamePattern()) + ", checkForUpdates=" + this.isCheckForUpdates() + ", allowCommands=" + String.valueOf(this.getAllowCommands()) + ", lifespan=" + String.valueOf(this.getLifespan()) + ", debug=" + this.isDebug() + ", preventKicking=" + String.valueOf(this.getPreventKicking()) + ", invseeImplement=" + String.valueOf(this.getInvseeImplement()) + ", defaultFeatures=" + String.valueOf(this.getDefaultFeatures()) + ", presets=" + String.valueOf(this.getPresets()) + ", actions=" + String.valueOf(this.getActions()) + ", arenas=" + String.valueOf(this.getArenas()) + ", pvpArenas=" + String.valueOf(this.getPvpArenas()) + ", gamemodes=" + String.valueOf(this.getGamemodes()) + ", matchTitleColor=" + String.valueOf(this.getMatchTitleColor()) + ", matchSubtitleColor=" + String.valueOf(this.getMatchSubtitleColor()) + ", countdownColor=" + String.valueOf(this.getCountdownColor()) + ", matchTitleText=" + this.getMatchTitleText() + ", matchSubtitleText=" + this.getMatchSubtitleText() + ", fightText=" + this.getFightText() + ", matchFoundSound=" + String.valueOf(this.getMatchFoundSound()) + ", matchFoundSoundVolume=" + this.getMatchFoundSoundVolume() + ", matchFoundSoundPitch=" + this.getMatchFoundSoundPitch() + ", countdownSound=" + String.valueOf(this.getCountdownSound()) + ", countdownSoundVolume=" + this.getCountdownSoundVolume() + ", countdownSoundPitch=" + this.getCountdownSoundPitch() + ", silentCommands=" + this.isSilentCommands() + ", matchWinMessage=" + this.getMatchWinMessage() + ", matchLoseMessage=" + this.getMatchLoseMessage() + ", playGuiTitle=" + this.getPlayGuiTitle() + ", pvpLobby=" + String.valueOf(this.getPvpLobby()) + ", pvpResultTeleportDelayTicks=" + this.getPvpResultTeleportDelayTicks() + ", arcadeSoundsEnabled=" + this.isArcadeSoundsEnabled() + ", arcadeSoundsMasterVolume=" + this.getArcadeSoundsMasterVolume() + ", arcadeMatchFoundSound=" + String.valueOf(this.getArcadeMatchFoundSound()) + ", arcadeMatchFoundPitch=" + this.getArcadeMatchFoundPitch() + ", arcadeCountdownSound=" + String.valueOf(this.getArcadeCountdownSound()) + ", arcadeCountdownPitch=" + this.getArcadeCountdownPitch() + ", arcadeFightSound=" + String.valueOf(this.getArcadeFightSound()) + ", arcadeFightPitch=" + this.getArcadeFightPitch() + ", arcadeWinSound=" + String.valueOf(this.getArcadeWinSound()) + ", arcadeWinPitch=" + this.getArcadeWinPitch() + ", arcadeLoseSound=" + String.valueOf(this.getArcadeLoseSound()) + ", arcadeLosePitch=" + this.getArcadeLosePitch() + ", botSoundsEnabled=" + this.isBotSoundsEnabled() + ", botSpawnSounds=" + String.valueOf(this.getBotSpawnSounds()) + ", botMoveSounds=" + String.valueOf(this.getBotMoveSounds()) + ", botHitSounds=" + String.valueOf(this.getBotHitSounds()) + ", botDeathSounds=" + String.valueOf(this.getBotDeathSounds()) + ", botMoveIntervalTicks=" + this.getBotMoveIntervalTicks() + ", botMoveMinDistance=" + this.getBotMoveMinDistance() + ", botSoundsMasterVolume=" + this.getBotSoundsMasterVolume() + ")";
    }
}
