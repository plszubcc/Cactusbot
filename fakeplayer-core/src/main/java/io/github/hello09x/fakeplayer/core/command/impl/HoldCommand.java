package io.github.hello09x.fakeplayer.core.command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.CommandArguments;
import io.github.hello09x.devtools.core.translation.TranslatorUtils;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerReplenishManager;
import java.util.Locale;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

@Singleton
public class HoldCommand extends AbstractCommand {
   private final FakeplayerReplenishManager replenishManager;

   @Inject
   public HoldCommand(FakeplayerReplenishManager replenishManager) {
      this.replenishManager = replenishManager;
   }

   public void hold(@NotNull CommandSender sender, @NotNull CommandArguments args) throws WrapperCommandSyntaxException {
      Player fake = super.getFakeplayer(sender, args);
      String selection = Objects.toString(args.get("selection"), "").trim();
      if (selection.isEmpty()) {
         throw this.invalidSelection(sender);
      } else if (selection.chars().allMatch(Character::isDigit)) {
         int slot = Integer.parseInt(selection);
         if (slot >= 1 && slot <= 9) {
            fake.getInventory().setHeldItemSlot(slot - 1);
         } else {
            throw this.invalidSelection(sender);
         }
      } else {
         Material material = this.resolveMaterial(selection);
         if (material != null && !material.isAir()) {
            this.selectItem(fake, material, sender);
            this.replenishManager.setReplenish(fake, true);
         } else {
            throw this.invalidSelection(sender);
         }
      }
   }

   private void selectItem(@NotNull Player fake, @NotNull Material material, @NotNull CommandSender sender) throws WrapperCommandSyntaxException {
      PlayerInventory inventory = fake.getInventory();
      int heldSlot = inventory.getHeldItemSlot();
      ItemStack heldItem = inventory.getItem(heldSlot);
      if (!this.matches(heldItem, material)) {
         int hotbarSlot = this.findItemSlot(inventory, material, 0, 8);
         if (hotbarSlot >= 0) {
            inventory.setHeldItemSlot(hotbarSlot);
         } else {
            int inventorySlot = this.findItemSlot(inventory, material, 9, 35);
            if (inventorySlot < 0) {
               throw this.itemNotFound(sender, material);
            } else {
               ItemStack targetStack = inventory.getItem(inventorySlot);
               ItemStack displaced = heldItem != null && !heldItem.getType().isAir() ? heldItem.clone() : null;
               inventory.setItem(heldSlot, targetStack);
               inventory.setItem(inventorySlot, displaced);
            }
         }
      }
   }

   private int findItemSlot(@NotNull PlayerInventory inventory, @NotNull Material material, int start, int end) {
      for (int slot = start; slot <= end; slot++) {
         if (this.matches(inventory.getItem(slot), material)) {
            return slot;
         }
      }

      return -1;
   }

   private boolean matches(ItemStack item, Material material) {
      return item != null && item.getType() == material && item.getAmount() > 0;
   }

   private Material resolveMaterial(@NotNull String input) {
      String normalized = this.normalizeMaterial(input);
      Material material = Material.matchMaterial(normalized);
      if (material != null) {
         return material;
      } else {
         return switch (normalized) {
            case "WINDBURST", "WINDCHARGE", "WIND_CHARGE_ITEM" -> Material.WIND_CHARGE;
            case "GAPPLE" -> Material.GOLDEN_APPLE;
            case "EGAPPLE", "ENCHANTED_GAPPLE" -> Material.ENCHANTED_GOLDEN_APPLE;
            default -> null;
         };
      }
   }

   private String normalizeMaterial(@NotNull String input) {
      return input.trim().toUpperCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
   }

   private WrapperCommandSyntaxException invalidSelection(@NotNull CommandSender sender) {
      return CommandAPI.failWithString(
         ComponentUtils.toString(Component.translatable("fakeplayer.command.hold.error.invalid-selection"), TranslatorUtils.getLocale(sender))
      );
   }

   private WrapperCommandSyntaxException itemNotFound(@NotNull CommandSender sender, @NotNull Material material) {
      return CommandAPI.failWithString(
         ComponentUtils.toString(Component.translatable("fakeplayer.command.hold.error.item-not-found", material.name()), TranslatorUtils.getLocale(sender))
      );
   }
}
