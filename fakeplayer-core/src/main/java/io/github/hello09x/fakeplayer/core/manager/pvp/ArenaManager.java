package io.github.hello09x.fakeplayer.core.manager.pvp;

import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.config.ArenaSpawn;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.config.GamemodeDefinition;
import io.github.hello09x.fakeplayer.core.config.PresetDefinition;
import io.github.hello09x.fakeplayer.core.config.PvpArenaDefinition;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import io.github.hello09x.fakeplayer.core.preset.PresetInventoryCodec;
import io.github.hello09x.fakeplayer.core.preset.PresetInventorySnapshot;
import io.github.hello09x.fakeplayer.core.util.Attributes;
import io.github.hello09x.fakeplayer.core.util.SkinRestorerCompat;
import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.Title.Times;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class ArenaManager {

    private static final long ROUND_END_DELAY_TICKS = 40L;
    private final FakeplayerConfig config;
    private final FakeplayerManager fakeplayerManager;
    private final ActionManager actionManager;
    private final Map<String, ArenaManager.ActiveMatch> active = new ConcurrentHashMap();

    @Inject
    public ArenaManager(FakeplayerConfig fakeplayerconfig, FakeplayerManager fakeplayermanager, ActionManager actionmanager) {
        super();
        this.config = fakeplayerconfig;
        this.fakeplayerManager = fakeplayermanager;
        this.actionManager = actionmanager;
    }

    public @Nullable PvpArenaDefinition get(@NotNull String s) {
        return (PvpArenaDefinition) this.config.getPvpArenas().get(s);
    }

    public @Nullable String getArenaOf(@NotNull Player player) {
        if (!player.hasMetadata("fakeplayer:arena_name")) {
            return null;
        } else {
            MetadataValue metadatavalue = (MetadataValue) Iterables.getFirst(player.getMetadata("fakeplayer:arena_name"), (Object) null);

            if (metadatavalue != null && metadatavalue.getOwningPlugin() == Main.getInstance()) {
                String s = metadatavalue.asString();

                return s != null && !s.isBlank() ? s : null;
            } else {
                return null;
            }
        }
    }

    public @Nullable Player getOpponent(@NotNull Player player) {
        String s = this.getArenaOf(player);

        if (s == null) {
            return null;
        } else {
            ArenaManager.ActiveMatch arenamanager_activematch = (ArenaManager.ActiveMatch) this.active.get(s);

            return arenamanager_activematch == null ? null : (player.getUniqueId().equals(arenamanager_activematch.playerId) ? (arenamanager_activematch.botId == null ? null : Bukkit.getPlayer(arenamanager_activematch.botId)) : (player.getUniqueId().equals(arenamanager_activematch.botId) ? Bukkit.getPlayer(arenamanager_activematch.playerId) : null));
        }
    }

    public boolean isBusy(@NotNull String s) {
        PvpArenaDefinition pvparenadefinition = (PvpArenaDefinition) this.config.getPvpArenas().get(s);

        return pvparenadefinition != null && pvparenadefinition.busy();
    }

    public @Nullable String findFreeArena(@NotNull List<String> list) {
        for (String s : list) {
            PvpArenaDefinition pvparenadefinition = (PvpArenaDefinition) this.config.getPvpArenas().get(s);

            if (pvparenadefinition != null && !pvparenadefinition.busy()) {
                return s;
            }
        }

        return null;
    }

    public boolean startMatch(@NotNull Player player, @NotNull String s) {
        PvpArenaDefinition pvparenadefinition = (PvpArenaDefinition) this.config.getPvpArenas().get(s);

        if (pvparenadefinition == null) {
            return false;
        } else if (pvparenadefinition.busy()) {
            return false;
        } else if (this.active.containsKey(s)) {
            return false;
        } else {
            this.setBusy(s, true);
            Location location = player.getLocation().clone();
            PresetInventorySnapshot presetinventorysnapshot = PresetInventoryCodec.capture(player);
            PresetInventorySnapshot presetinventorysnapshot1 = null;
            Object object = null;

            if (pvparenadefinition.preset() != null && this.config.getPresets().containsKey(pvparenadefinition.preset())) {
                PresetDefinition presetdefinition = (PresetDefinition) this.config.getPresets().get(pvparenadefinition.preset());

                presetinventorysnapshot1 = PresetInventoryCodec.decode(presetdefinition.inventoryBase64());
            }

            PresetInventorySnapshot presetinventorysnapshot2 = PresetInventoryCodec.decode(pvparenadefinition.playerInventoryBase64());

            player.setMetadata("fakeplayer:arena_return", new FixedMetadataValue(Main.getInstance(), location));
            player.setMetadata("fakeplayer:arena_name", new FixedMetadataValue(Main.getInstance(), s));
            ArenaManager.ActiveMatch arenamanager_activematch = new ArenaManager.ActiveMatch(player.getUniqueId(), location, presetinventorysnapshot, presetinventorysnapshot1, presetinventorysnapshot2, Math.max(1, pvparenadefinition.rounds()));

            this.active.put(s, arenamanager_activematch);
            this.spawnRound(s, pvparenadefinition, arenamanager_activematch, true);
            return true;
        }
    }

    public void endMatch(@NotNull String s) {
        this.endMatch(s, (ArenaManager.MatchResult) null);
    }

    public void endMatch(@NotNull String s, ArenaManager.@Nullable MatchResult arenamanager_matchresult) {
        ArenaManager.ActiveMatch arenamanager_activematch = (ArenaManager.ActiveMatch) this.active.remove(s);

        this.setBusy(s, false);
        if (arenamanager_activematch != null) {
            this.removeLiveBotImmediately(arenamanager_activematch, "Arena match ended");
            Player player = Bukkit.getPlayer(arenamanager_activematch.playerId);

            if (player != null) {
                player.removeMetadata("fakeplayer:arena_name", Main.getInstance());
                player.removeMetadata("fakeplayer:arena_return", Main.getInstance());
                if (arenamanager_matchresult != null) {
                    this.sendMatchResult(player, s, arenamanager_activematch, arenamanager_matchresult);
                }

                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    this.resetPlayerState(player);
                    this.restoreSnapshot(player, arenamanager_activematch.returnInventory);
                    player.teleport(this.getExitLocation(arenamanager_activematch.returnLocation));
                }, 0L);
            }

        }
    }

    public void endMatchOf(@NotNull Player player) {
        String s = this.getArenaOf(player);

        if (s != null) {
            this.endMatch(s);
        }
    }

    public void handleParticipantQuit(@NotNull Player player) {
        String s = this.getArenaOf(player);

        if (s != null) {
            ArenaManager.ActiveMatch arenamanager_activematch = (ArenaManager.ActiveMatch) this.active.get(s);

            if (arenamanager_activematch != null) {
                if (!player.getUniqueId().equals(arenamanager_activematch.botId) || !arenamanager_activematch.resolvingRound) {
                    this.endMatch(s);
                }
            }
        }
    }

    public void resolveRoundLoss(@NotNull Player player) {
        String s = this.getArenaOf(player);

        if (s != null) {
            ArenaManager.ActiveMatch arenamanager_activematch = (ArenaManager.ActiveMatch) this.active.get(s);
            PvpArenaDefinition pvparenadefinition = (PvpArenaDefinition) this.config.getPvpArenas().get(s);

            if (arenamanager_activematch != null && pvparenadefinition != null && !arenamanager_activematch.resolvingRound) {
                arenamanager_activematch.resolvingRound = true;
                UUID uuid = player.getUniqueId();

                if (uuid.equals(arenamanager_activematch.playerId)) {
                    ++arenamanager_activematch.botRoundsWon;
                } else {
                    if (!uuid.equals(arenamanager_activematch.botId)) {
                        arenamanager_activematch.resolvingRound = false;
                        return;
                    }

                    ++arenamanager_activematch.playerRoundsWon;
                }

                Player player1 = Bukkit.getPlayer(arenamanager_activematch.playerId);

                if (arenamanager_activematch.botId == null) {
                    Object object = null;
                } else {
                    Bukkit.getPlayer(arenamanager_activematch.botId);
                }

                if (player1 != null && player1.isOnline()) {
                    if (arenamanager_activematch.playerRoundsWon < arenamanager_activematch.roundsToWin && arenamanager_activematch.botRoundsWon < arenamanager_activematch.roundsToWin) {
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                            Player player2 = arenamanager_activematch.botId == null ? null : Bukkit.getPlayer(arenamanager_activematch.botId);

                            if (player2 != null && player2.isOnline()) {
                                this.fakeplayerManager.remove(player2.getName(), "Round completed");
                            }

                            this.spawnRound(s, pvparenadefinition, arenamanager_activematch, false);
                        }, 40L);
                    } else {
                        ArenaManager.MatchResult arenamanager_matchresult = arenamanager_activematch.playerRoundsWon >= arenamanager_activematch.roundsToWin ? ArenaManager.MatchResult.WIN : ArenaManager.MatchResult.LOSE;

                        this.endMatch(s, arenamanager_matchresult);
                    }
                } else {
                    this.endMatch(s);
                }
            }
        }
    }

    private void showCountdown(@NotNull Player player) {
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            TextComponent textcomponent = (TextComponent) Component.text(this.config.getMatchTitleText()).color(this.config.getMatchTitleColor());
            TextComponent textcomponent1 = (TextComponent) Component.text(this.config.getMatchSubtitleText().formatted(3)).color(this.config.getMatchSubtitleColor());

            player.showTitle(Title.title(textcomponent, textcomponent1, Times.times(Duration.ofMillis(250L), Duration.ofSeconds(1L), Duration.ofMillis(250L))));
            if (this.config.getMatchFoundSound() != null) {
                player.playSound(player.getLocation(), this.config.getMatchFoundSound(), this.config.getMatchFoundSoundVolume(), this.config.getMatchFoundSoundPitch());
            }

            this.playArcadeSound(player, this.config.getArcadeMatchFoundSound(), this.config.getArcadeMatchFoundPitch());
        });
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (player.isOnline()) {
                player.showTitle(Title.title(Component.empty(), Component.text("2").color(this.config.getCountdownColor()), Times.times(Duration.ZERO, Duration.ofSeconds(1L), Duration.ZERO)));
                this.playCountdownSound(player);
            }

        }, 20L);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (player.isOnline()) {
                player.showTitle(Title.title(Component.empty(), Component.text("1").color(this.config.getCountdownColor()), Times.times(Duration.ZERO, Duration.ofSeconds(1L), Duration.ZERO)));
                this.playCountdownSound(player);
            }

        }, 40L);
        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (player.isOnline()) {
                player.showTitle(Title.title(Component.empty(), Component.text(this.config.getFightText()).color(this.config.getCountdownColor()), Times.times(Duration.ZERO, Duration.ofSeconds(1L), Duration.ofMillis(500L))));
                this.playArcadeSound(player, this.config.getArcadeFightSound(), this.config.getArcadeFightPitch());
            }

        }, 60L);
    }

    private void setBusy(@NotNull String s, boolean flag) {
        File file = this.getConfigFile();
        YamlConfiguration yamlconfiguration = YamlConfiguration.loadConfiguration(file);

        yamlconfiguration.set("arenas." + s + ".busy", flag);

        try {
            yamlconfiguration.save(file);
        } catch (Exception exception) {
            ;
        }

        this.config.reload();
    }

    public void writeSpawn(@NotNull String s, @NotNull String s1, @NotNull ArenaSpawn arenaspawn) throws Exception {
        File file = this.getConfigFile();
        YamlConfiguration yamlconfiguration = YamlConfiguration.loadConfiguration(file);

        yamlconfiguration.set("arenas." + s + ".spawn." + s1, this.toCsv(arenaspawn));
        yamlconfiguration.save(file);
        this.config.reload();
    }

    public void writePreset(@NotNull String s, @NotNull String s1) throws Exception {
        File file = this.getConfigFile();
        YamlConfiguration yamlconfiguration = YamlConfiguration.loadConfiguration(file);

        yamlconfiguration.set("arenas." + s + ".preset", s1);
        yamlconfiguration.save(file);
        this.config.reload();
    }

    public void writeBot(@NotNull String s, @NotNull String s1, @Nullable String s2) throws Exception {
        File file = this.getConfigFile();
        YamlConfiguration yamlconfiguration = YamlConfiguration.loadConfiguration(file);

        yamlconfiguration.set("arenas." + s + ".bot", s1);
        yamlconfiguration.set("arenas." + s + ".skin", s2 == null ? "" : s2);
        yamlconfiguration.save(file);
        this.config.reload();
    }

    public void writeRounds(@NotNull String s, int i) throws Exception {
        File file = this.getConfigFile();
        YamlConfiguration yamlconfiguration = YamlConfiguration.loadConfiguration(file);

        yamlconfiguration.set("arenas." + s + ".rounds", Math.max(1, i));
        yamlconfiguration.save(file);
        this.config.reload();
    }

    public void writeLobby(@NotNull ArenaSpawn arenaspawn) throws Exception {
        File file = this.getConfigFile();
        YamlConfiguration yamlconfiguration = YamlConfiguration.loadConfiguration(file);

        yamlconfiguration.set("pvp.lobby", this.toCsv(arenaspawn));
        yamlconfiguration.save(file);
        this.config.reload();
    }

    public void writePlayerInventory(@NotNull String s, @NotNull PresetInventorySnapshot presetinventorysnapshot) throws Exception {
        String s1 = PresetInventoryCodec.encode(presetinventorysnapshot);

        if (s1 == null) {
            throw new IllegalStateException("encode-failed");
        } else {
            File file = this.getConfigFile();
            YamlConfiguration yamlconfiguration = YamlConfiguration.loadConfiguration(file);

            yamlconfiguration.set("arenas." + s + ".player-inventory", s1);
            yamlconfiguration.save(file);
            this.config.reload();
        }
    }

    public void createArena(@NotNull String s, @NotNull Location location) throws Exception {
        File file = this.getConfigFile();
        YamlConfiguration yamlconfiguration = YamlConfiguration.loadConfiguration(file);

        if (yamlconfiguration.getConfigurationSection("arenas") == null) {
            yamlconfiguration.createSection("arenas");
        }

        if (yamlconfiguration.getConfigurationSection("arenas." + s) != null) {
            throw new IllegalStateException("exists");
        } else {
            ArenaSpawn arenaspawn = ArenaSpawn.fromLocation(location);

            yamlconfiguration.set("arenas." + s + ".preset", "");
            yamlconfiguration.set("arenas." + s + ".bot", "Bot1");
            yamlconfiguration.set("arenas." + s + ".skin", "");
            yamlconfiguration.set("arenas." + s + ".player-inventory", "");
            yamlconfiguration.set("arenas." + s + ".rounds", 1);
            yamlconfiguration.set("arenas." + s + ".spawn.player", this.toCsv(arenaspawn));
            yamlconfiguration.set("arenas." + s + ".spawn.bot", this.toCsv(arenaspawn));
            yamlconfiguration.set("arenas." + s + ".busy", false);
            yamlconfiguration.save(file);
            this.config.reload();
        }
    }

    public void deleteArena(@NotNull String s) throws Exception {
        File file = this.getConfigFile();
        YamlConfiguration yamlconfiguration = YamlConfiguration.loadConfiguration(file);

        if (!yamlconfiguration.isConfigurationSection("arenas." + s)) {
            throw new IllegalStateException("missing");
        } else {
            yamlconfiguration.set("arenas." + s, (Object) null);
            yamlconfiguration.save(file);
            this.config.reload();
        }
    }

    private @NotNull String toCsv(@NotNull ArenaSpawn arenaspawn) {
        return "%s,%s,%s,%s,%s,%s".formatted(arenaspawn.world(), arenaspawn.x(), arenaspawn.y(), arenaspawn.z(), arenaspawn.yaw(), arenaspawn.pitch());
    }

    private @NotNull File getConfigFile() {
        Main main = Main.getInstance();
        File file = main.getDataFolder();

        if (!file.exists()) {
            file.mkdirs();
        }

        return new File(file, "config.yml");
    }

    private void applySnapshot(@NotNull Player player, @NotNull PresetInventorySnapshot presetinventorysnapshot) {
        PlayerInventory playerinventory = player.getInventory();

        playerinventory.setStorageContents(presetinventorysnapshot.storageContents());
        playerinventory.setArmorContents(presetinventorysnapshot.armorContents());
        playerinventory.setItemInOffHand(presetinventorysnapshot.offhand());
        playerinventory.setHeldItemSlot(Math.max(0, Math.min(8, presetinventorysnapshot.heldSlot())));
        player.updateInventory();
    }

    private void restoreSnapshot(@NotNull Player player, @NotNull PresetInventorySnapshot presetinventorysnapshot) {
        this.applySnapshot(player, presetinventorysnapshot);
    }

    private void playCountdownSound(@NotNull Player player) {
        if (this.config.getCountdownSound() != null) {
            player.playSound(player.getLocation(), this.config.getCountdownSound(), this.config.getCountdownSoundVolume(), this.config.getCountdownSoundPitch());
        }

        this.playArcadeSound(player, this.config.getArcadeCountdownSound(), this.config.getArcadeCountdownPitch());
    }

    private void applyPresetToPlayer(@NotNull Player player, @Nullable PresetInventorySnapshot presetinventorysnapshot) {
        if (presetinventorysnapshot != null) {
            this.applySnapshot(player, presetinventorysnapshot);
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (player.isOnline() && player.hasMetadata("fakeplayer:arena_name")) {
                    this.applySnapshot(player, presetinventorysnapshot);
                }

            }, 1L);
        }
    }

    private void spawnRound(@NotNull String s, @NotNull PvpArenaDefinition pvparenadefinition, ArenaManager.@NotNull ActiveMatch arenamanager_activematch, boolean flag) {
        Player player = Bukkit.getPlayer(arenamanager_activematch.playerId);

        if (player != null && player.isOnline()) {
            Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                this.resetPlayerState(player);
                player.teleport(pvparenadefinition.playerSpawn().toLocation());
                this.applyPresetToPlayer(player, arenamanager_activematch.playerSnapshot);
            });
            Long olong = (Long) Optional.ofNullable(this.config.getLifespan()).map(Duration::toMillis).orElse(-1L);

            this.fakeplayerManager.spawnAsync(player, pvparenadefinition.botName().isBlank() ? null : pvparenadefinition.botName(), pvparenadefinition.botSpawn().toLocation(), olong).thenAcceptAsync((player1) -> {
                if (player1 == null) {
                    this.endMatch(s);
                } else {
                    Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
                        player1.setMetadata("fakeplayer:arena_name", new FixedMetadataValue(Main.getInstance(), s));
                        if (pvparenadefinition.preset() != null && this.config.getPresets().containsKey(pvparenadefinition.preset())) {
                            player1.setMetadata("fakeplayer:preset", new FixedMetadataValue(Main.getInstance(), pvparenadefinition.preset()));
                        }

                        if (arenamanager_activematch.presetSnapshot != null) {
                            this.applySnapshot(player1, arenamanager_activematch.presetSnapshot);
                            this.applyConfiguredReach(player1, (PresetDefinition) this.config.getPresets().get(pvparenadefinition.preset()));
                        } else if (pvparenadefinition.preset() != null && this.config.getPresets().containsKey(pvparenadefinition.preset())) {
                            this.applyConfiguredReach(player1, (PresetDefinition) this.config.getPresets().get(pvparenadefinition.preset()));
                        }

                        this.applyPresetToPlayer(player, arenamanager_activematch.playerSnapshot);
                        arenamanager_activematch.botId = player1.getUniqueId();
                        arenamanager_activematch.resolvingRound = false;
                        if (flag) {
                            this.showCountdown(player);
                        }

                    });
                    if (pvparenadefinition.botSkin() != null && !pvparenadefinition.botSkin().isBlank() && SkinRestorerCompat.isAvailable()) {
                        SkinRestorerCompat.applySkinByNameAsync(Main.getInstance(), player1, pvparenadefinition.botSkin());
                        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                            if (player1.isOnline() && this.fakeplayerManager.isFake(player1)) {
                                SkinRestorerCompat.applySkinByNameAsync(Main.getInstance(), player1, pvparenadefinition.botSkin());
                            }

                        }, 20L);
                    }

                }
            });
        } else {
            this.endMatch(s);
        }
    }

    private void resetPlayerState(@NotNull Player player) {
        player.setFireTicks(0);
        player.setFallDistance(0.0F);
        player.setVelocity(new Vector(0, 0, 0));
        player.setFoodLevel(20);
        player.setSaturation(20.0F);
        player.setExhaustion(0.0F);
        player.setRemainingAir(player.getMaximumAir());
        double d0 = 20.0D;

        try {
            AttributeInstance attributeinstance = player.getAttribute(Attributes.maxHealth());

            if (attributeinstance != null) {
                d0 = attributeinstance.getValue();
            }
        } catch (Throwable throwable) {
            ;
        }

        try {
            player.setHealth(Math.max(1.0D, d0));
        } catch (Throwable throwable1) {
            ;
        }

    }

    private void applyConfiguredReach(@NotNull Player player, @NotNull PresetDefinition presetdefinition) {
        Double odouble = presetdefinition.reach();

        if (odouble != null && odouble > 0.0D) {
            player.setMetadata("fakeplayer:preset_reach", new FixedMetadataValue(Main.getInstance(), odouble));

            try {
                AttributeInstance attributeinstance = player.getAttribute(Attributes.playerEntityInteractionRange());

                if (attributeinstance != null) {
                    attributeinstance.setBaseValue(odouble);
                }
            } catch (Throwable throwable) {
                ;
            }

        } else {
            player.removeMetadata("fakeplayer:preset_reach", Main.getInstance());
        }
    }

    private void sendMatchResult(@NotNull Player player, @NotNull String s, ArenaManager.@NotNull ActiveMatch arenamanager_activematch, ArenaManager.@NotNull MatchResult arenamanager_matchresult) {
        PvpArenaDefinition pvparenadefinition = (PvpArenaDefinition) this.config.getPvpArenas().get(s);
        String s1 = pvparenadefinition == null ? "" : pvparenadefinition.botName();
        String s2 = (String) this.config.getGamemodes().values().stream().filter((gamemodedefinition) -> {
            return gamemodedefinition.arenas().contains(s);
        }).map(GamemodeDefinition::name).findFirst().orElse("");
        HashMap hashmap = new HashMap();

        hashmap.put("%arena%", s);
        hashmap.put("%bot%", s1);
        hashmap.put("%gamemode%", s2);
        hashmap.put("%player_wins%", Integer.toString(arenamanager_activematch.playerRoundsWon));
        hashmap.put("%bot_wins%", Integer.toString(arenamanager_activematch.botRoundsWon));
        hashmap.put("%rounds%", Integer.toString(arenamanager_activematch.roundsToWin));
        String s3 = arenamanager_matchresult == ArenaManager.MatchResult.WIN ? this.config.getMatchWinMessage() : this.config.getMatchLoseMessage();
        Component component = this.config.formatConfiguredMessage(s3, hashmap);

        player.showTitle(Title.title(component, Component.text(arenamanager_activematch.playerRoundsWon + " - " + arenamanager_activematch.botRoundsWon), Times.times(Duration.ofMillis(150L), Duration.ofSeconds(2L), Duration.ofMillis(500L))));
        this.playArcadeSound(player, arenamanager_matchresult == ArenaManager.MatchResult.WIN ? this.config.getArcadeWinSound() : this.config.getArcadeLoseSound(), arenamanager_matchresult == ArenaManager.MatchResult.WIN ? this.config.getArcadeWinPitch() : this.config.getArcadeLosePitch());
    }

    private void playVictoryAnimation(@NotNull Player player) {
        for (int i = 0; i < 8; ++i) {
            long j = (long) i * 4L;

            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (player.isOnline()) {
                    player.setSneaking(!player.isSneaking());
                    Location location = player.getLocation().add(0.0D, 1.0D, 0.0D);

                    player.getWorld().spawnParticle(Particle.TOTEM_OF_UNDYING, location, 18, 0.35D, 0.5D, 0.35D, 0.02D);
                    player.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location, 10, 0.4D, 0.6D, 0.4D, 0.01D);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8F, 1.2F);
                }
            }, j);
        }

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (player.isOnline()) {
                player.setSneaking(false);
            }

        }, 36L);
    }

    private void playArcadeSound(@NotNull Player player, @Nullable Sound sound, float f) {
        if (this.config.isArcadeSoundsEnabled() && sound != null) {
            float f1 = this.config.getArcadeSoundsMasterVolume();

            if (f1 > 0.0F) {
                player.playSound(player.getLocation(), sound, f1, f);
            }
        }
    }

    public void sendPlayerToLobby(@NotNull Player player) {
        Bukkit.getScheduler().runTask(Main.getInstance(), () -> {
            player.teleport(this.getExitLocation(player.getLocation()));
        });
    }

    private @NotNull Location getExitLocation(@NotNull Location location) {
        ArenaSpawn arenaspawn = this.config.getPvpLobby();

        if (arenaspawn == null) {
            return location;
        } else {
            Location location1 = arenaspawn.toLocation();

            return location1 == null ? location : location1;
        }
    }

    private void removeLiveBotImmediately(ArenaManager.@NotNull ActiveMatch arenamanager_activematch, @NotNull String s) {
        Player player = arenamanager_activematch.botId == null ? null : Bukkit.getPlayer(arenamanager_activematch.botId);

        if (player != null && player.isOnline()) {
            this.stopBotAutomation(player);
            this.fakeplayerManager.remove(player.getName(), s);
        }
    }

    private void stopBotAutomation(@NotNull Player player) {
        this.actionManager.stop(player);
        player.removeMetadata("fakeplayer:preset", Main.getInstance());
        player.removeMetadata("fakeplayer:arena_name", Main.getInstance());
        player.removeMetadata("fakeplayer:arena_return", Main.getInstance());
        player.setVelocity(new Vector(0, 0, 0));
        player.setSprinting(false);
        player.setSneaking(false);
        player.setFireTicks(0);
    }

    private static enum MatchResult {

        WIN, LOSE;

        private MatchResult() {}
    }

    private static final class ActiveMatch {

        private final @NotNull UUID playerId;
        private @Nullable UUID botId;
        private final @NotNull Location returnLocation;
        private final @NotNull PresetInventorySnapshot returnInventory;
        private final @Nullable PresetInventorySnapshot presetSnapshot;
        private final @Nullable PresetInventorySnapshot playerSnapshot;
        private final int roundsToWin;
        private int playerRoundsWon;
        private int botRoundsWon;
        private boolean resolvingRound;

        private ActiveMatch(@NotNull UUID playerId, @NotNull Location returnLocation, @NotNull PresetInventorySnapshot returnInventory, @Nullable PresetInventorySnapshot presetSnapshot, @Nullable PresetInventorySnapshot playerSnapshot, int roundsToWin) {
            super();
            this.playerId = playerId;
            this.returnLocation = returnLocation;
            this.returnInventory = returnInventory;
            this.presetSnapshot = presetSnapshot;
            this.playerSnapshot = playerSnapshot;
            this.roundsToWin = roundsToWin;
        }
    }
}
