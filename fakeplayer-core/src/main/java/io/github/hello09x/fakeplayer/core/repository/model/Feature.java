package io.github.hello09x.fakeplayer.core.repository.model;

import io.github.hello09x.fakeplayer.api.spi.ActionSetting;
import io.github.hello09x.fakeplayer.api.spi.ActionType;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerAutofishManager;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerReplenishManager;
import io.github.hello09x.fakeplayer.core.manager.action.ActionManager;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.Generated;
import net.kyori.adventure.translation.Translatable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Feature implements Translatable, Singletons {

    collidable("fakeplayer.config.collidable", List.of("cactusbot.command.config"), List.of("true", "false"), "true", (fakerx) -> {
        return String.valueOf(fakerx.isCollidable());
    }, (fakerx, valuex) -> {
        fakerx.setCollidable(Boolean.parseBoolean(valuex));
    }), invulnerable("fakeplayer.config.invulnerable", List.of("cactusbot.command.config"), List.of("true", "false"), "false", (fakerx) -> {
        return String.valueOf(fakerx.isInvulnerable());
    }, (fakerx, valuex) -> {
        fakerx.setInvulnerable(Boolean.parseBoolean(valuex));
    }), wolverine("fakeplayer.config.wolverine", List.of("cactusbot.command.config"), List.of("true", "false"), "false", (fakex) -> {
        return String.valueOf(Optional.ofNullable(fakex.getPotionEffect(PotionEffectType.REGENERATION)).map(PotionEffect::isInfinite).orElse(false));
    }, (fakerx, valuex) -> {
        if (Boolean.parseBoolean(valuex)) {
            fakerx.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 4, true, true));
        } else {
            fakerx.removePotionEffect(PotionEffectType.REGENERATION);
        }

    }), look_at_entity("fakeplayer.config.look_at_entity", List.of("cactusbot.command.config", "cactusbot.command.look"), List.of("true", "false"), "false", (fakerx) -> {
        return String.valueOf(((ActionManager) Feature.actionManager.get()).hasActiveAction(fakerx, ActionType.LOOK_AT_NEAREST_ENTITY));
    }, (fakerx, valuex) -> {
        if (Boolean.parseBoolean(valuex)) {
            ((ActionManager) Feature.actionManager.get()).setAction(fakerx, ActionType.LOOK_AT_NEAREST_ENTITY, ActionSetting.continuous());
        } else {
            ((ActionManager) Feature.actionManager.get()).setAction(fakerx, ActionType.LOOK_AT_NEAREST_ENTITY, ActionSetting.stop());
        }

    }), pickup_items("fakeplayer.config.pickup_items", List.of("cactusbot.command.config"), List.of("true", "false"), "true", (fakerx) -> {
        return String.valueOf(fakerx.getCanPickupItems());
    }, (fakerx, valuex) -> {
        fakerx.setCanPickupItems(Boolean.parseBoolean(valuex));
    }), skin("fakeplayer.config.skin", List.of("cactusbot.command.config"), List.of("true", "false"), "true", (Function) null, (BiConsumer) null), replenish("fakeplayer.config.replenish", List.of("cactusbot.command.config", "cactusbot.config.replenish"), List.of("true", "false"), "false", (fakerx) -> {
        return String.valueOf(((FakeplayerReplenishManager) Feature.replenishManager.get()).isReplenish(fakerx));
    }, (fakerx, valuex) -> {
        ((FakeplayerReplenishManager) Feature.replenishManager.get()).setReplenish(fakerx, Boolean.parseBoolean(valuex));
    }), autofish("fakeplayer.config.autofish", List.of("cactusbot.command.config", "cactusbot.config.autofish"), List.of("true", "false"), "false", (fakerx) -> {
        return String.valueOf(((FakeplayerAutofishManager) Feature.autofishManager.get()).isAutofish(fakerx));
    }, (fakerx, valuex) -> {
        ((FakeplayerAutofishManager) Feature.autofishManager.get()).setAutofish(fakerx, Boolean.parseBoolean(valuex));
    });

    final @NotNull String translationKey;
    final @NotNull List<String> permissions;
    final @NotNull List<String> options;
    final @NotNull String defaultOption;
    final @Nullable Function<Player, String> detector;
    final @Nullable BiConsumer<Player, String> modifier;

    public @NotNull String translationKey() {
        return this.translationKey;
    }

    public boolean hasDetector() {
        return this.detector != null;
    }

    public boolean hasModifier() {
        return this.modifier != null;
    }

    public boolean testPermissions(@NotNull CommandSender sender) {
        if (this.permissions.isEmpty()) {
            return true;
        } else {
            for (String permission : this.permissions) {
                if (!sender.hasPermission(permission)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Generated
    public @NotNull String getTranslationKey() {
        return this.translationKey;
    }

    @Generated
    public @NotNull List<String> getPermissions() {
        return this.permissions;
    }

    @Generated
    public @NotNull List<String> getOptions() {
        return this.options;
    }

    @Generated
    public @NotNull String getDefaultOption() {
        return this.defaultOption;
    }

    @Generated
    public @Nullable Function<Player, String> getDetector() {
        return this.detector;
    }

    @Generated
    public @Nullable BiConsumer<Player, String> getModifier() {
        return this.modifier;
    }

    @Generated
    private Feature(@NotNull String translationKey, @NotNull List<String> permissions, @Nullable List<String> options, @Nullable String defaultOption, Function<Player, String> detector, BiConsumer<Player, String> modifier) {
        this.translationKey = translationKey;
        this.permissions = permissions;
        this.options = options;
        this.defaultOption = defaultOption;
        this.detector = detector;
        this.modifier = modifier;
    }
}
