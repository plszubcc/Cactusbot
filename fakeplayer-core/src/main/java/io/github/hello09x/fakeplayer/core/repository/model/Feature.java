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
   collidable(
      "fakeplayer.config.collidable",
      List.of("cactusbot.command.config"),
      List.of("true", "false"),
      "true",
      faker -> String.valueOf(faker.isCollidable()),
      (faker, value) -> faker.setCollidable(Boolean.parseBoolean(value))
   ),
   invulnerable(
      "fakeplayer.config.invulnerable",
      List.of("cactusbot.command.config"),
      List.of("true", "false"),
      "false",
      faker -> String.valueOf(faker.isInvulnerable()),
      (faker, value) -> faker.setInvulnerable(Boolean.parseBoolean(value))
   ),
   wolverine(
      "fakeplayer.config.wolverine",
      List.of("cactusbot.command.config"),
      List.of("true", "false"),
      "false",
      fake -> String.valueOf(Optional.ofNullable(fake.getPotionEffect(PotionEffectType.REGENERATION)).<Boolean>map(PotionEffect::isInfinite).orElse(false)),
      (faker, value) -> {
         if (Boolean.parseBoolean(value)) {
            faker.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, -1, 4, true, true));
         } else {
            faker.removePotionEffect(PotionEffectType.REGENERATION);
         }
      }
   ),
   look_at_entity(
      "fakeplayer.config.look_at_entity",
      List.of("cactusbot.command.config", "cactusbot.command.look"),
      List.of("true", "false"),
      "false",
      faker -> String.valueOf(((ActionManager)actionManager.get()).hasActiveAction(faker, ActionType.LOOK_AT_NEAREST_ENTITY)),
      (faker, value) -> {
         if (Boolean.parseBoolean(value)) {
            ((ActionManager)actionManager.get()).setAction(faker, ActionType.LOOK_AT_NEAREST_ENTITY, ActionSetting.continuous());
         } else {
            ((ActionManager)actionManager.get()).setAction(faker, ActionType.LOOK_AT_NEAREST_ENTITY, ActionSetting.stop());
         }
      }
   ),
   pickup_items(
      "fakeplayer.config.pickup_items",
      List.of("cactusbot.command.config"),
      List.of("true", "false"),
      "true",
      faker -> String.valueOf(faker.getCanPickupItems()),
      (faker, value) -> faker.setCanPickupItems(Boolean.parseBoolean(value))
   ),
   skin("fakeplayer.config.skin", List.of("cactusbot.command.config"), List.of("true", "false"), "true", null, null),
   replenish(
      "fakeplayer.config.replenish",
      List.of("cactusbot.command.config", "cactusbot.config.replenish"),
      List.of("true", "false"),
      "false",
      faker -> String.valueOf(((FakeplayerReplenishManager)replenishManager.get()).isReplenish(faker)),
      (faker, value) -> ((FakeplayerReplenishManager)replenishManager.get()).setReplenish(faker, Boolean.parseBoolean(value))
   ),
   autofish(
      "fakeplayer.config.autofish",
      List.of("cactusbot.command.config", "cactusbot.config.autofish"),
      List.of("true", "false"),
      "false",
      faker -> String.valueOf(((FakeplayerAutofishManager)autofishManager.get()).isAutofish(faker)),
      (faker, value) -> ((FakeplayerAutofishManager)autofishManager.get()).setAutofish(faker, Boolean.parseBoolean(value))
   );

   @NotNull
   final String translationKey;
   @NotNull
   final List<String> permissions;
   @NotNull
   final List<String> options;
   @NotNull
   final String defaultOption;
   @Nullable
   final Function<Player, String> detector;
   @Nullable
   final BiConsumer<Player, String> modifier;

   @NotNull
   public String translationKey() {
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

   @NotNull
   @Generated
   public String getTranslationKey() {
      return this.translationKey;
   }

   @NotNull
   @Generated
   public List<String> getPermissions() {
      return this.permissions;
   }

   @NotNull
   @Generated
   public List<String> getOptions() {
      return this.options;
   }

   @NotNull
   @Generated
   public String getDefaultOption() {
      return this.defaultOption;
   }

   @Nullable
   @Generated
   public Function<Player, String> getDetector() {
      return this.detector;
   }

   @Nullable
   @Generated
   public BiConsumer<Player, String> getModifier() {
      return this.modifier;
   }

   @Generated
   private Feature(
      @NotNull final String translationKey,
      @NotNull final List<String> permissions,
      @NotNull final List<String> options,
      @NotNull final String defaultOption,
      @Nullable final Function<Player, String> detector,
      @Nullable final BiConsumer<Player, String> modifier
   ) {
      if (translationKey == null) {
         throw new NullPointerException("translationKey is marked non-null but is null");
      } else if (permissions == null) {
         throw new NullPointerException("permissions is marked non-null but is null");
      } else if (options == null) {
         throw new NullPointerException("options is marked non-null but is null");
      } else if (defaultOption == null) {
         throw new NullPointerException("defaultOption is marked non-null but is null");
      } else {
         this.translationKey = translationKey;
         this.permissions = permissions;
         this.options = options;
         this.defaultOption = defaultOption;
         this.detector = detector;
         this.modifier = modifier;
      }
   }
}
