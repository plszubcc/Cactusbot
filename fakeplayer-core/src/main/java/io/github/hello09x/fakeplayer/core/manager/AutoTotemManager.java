package io.github.hello09x.fakeplayer.core.manager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;

@Singleton
public class AutoTotemManager implements Listener {

    private final FakeplayerManager fakeplayerManager;

    @Inject
    public AutoTotemManager(FakeplayerManager fakeplayerManager) {
        super();
        this.fakeplayerManager = fakeplayerManager;
    }

    public boolean toggle(@NotNull Player player) {
        boolean enabled = !this.isEnabled(player);

        this.setEnabled(player, enabled);
        return enabled;
    }

    public boolean isEnabled(@NotNull Player player) {
        return player.hasMetadata("fakeplayer:auto_totem") && player.getMetadata("fakeplayer:auto_totem").stream().anyMatch((valuex) -> {
            return valuex.asBoolean();
        });
    }

    public void setEnabled(@NotNull Player player, boolean enabled) {
        if (enabled) {
            player.setMetadata("fakeplayer:auto_totem", new FixedMetadataValue(Main.getInstance(), true));
            this.ensureTotem(player);
        } else {
            player.removeMetadata("fakeplayer:auto_totem", Main.getInstance());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onDamage(@NotNull EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player player) {
            if (!this.fakeplayerManager.isNotFake(player) && this.isEnabled(player)) {
                this.ensureTotem(player);
                return;
            }
        }

    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onResurrect(@NotNull EntityResurrectEvent event) {
        LivingEntity livingentity = event.getEntity();

        if (livingentity instanceof Player player) {
            if (!this.fakeplayerManager.isNotFake(player) && this.isEnabled(player)) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    this.ensureTotem(player);
                }, 1L);
                return;
            }
        }

    }

    public boolean ensureTotem(@NotNull Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack offhand = inventory.getItemInOffHand();

        if (this.isTotem(offhand)) {
            return true;
        } else {
            int slot = this.findTotemSlot(inventory);

            if (slot < 0) {
                return false;
            } else {
                ItemStack totem = inventory.getItem(slot);

                if (!this.isTotem(totem)) {
                    return false;
                } else {
                    ItemStack previousOffhand = offhand != null && !offhand.getType().isAir() ? offhand.clone() : null;

                    inventory.setItemInOffHand(totem.clone());
                    inventory.setItem(slot, previousOffhand);
                    return true;
                }
            }
        }
    }

    private int findTotemSlot(@NotNull PlayerInventory inventory) {
        for (int slot = 0; slot < 36; ++slot) {
            if (this.isTotem(inventory.getItem(slot))) {
                return slot;
            }
        }

        return -1;
    }

    private boolean isTotem(ItemStack item) {
        return item != null && item.getType() == Material.TOTEM_OF_UNDYING && item.getAmount() > 0;
    }
}
