package io.github.hello09x.fakeplayer.core.manager;

import com.destroystokyo.paper.event.player.PlayerLaunchProjectileEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.utils.BlockUtils;
import io.github.hello09x.fakeplayer.core.Main;
import java.util.Optional;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent.Reason;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class FakeplayerReplenishManager implements Listener {

    private final FakeplayerManager manager;

    @Inject
    public FakeplayerReplenishManager(FakeplayerManager manager) {
        super();
        this.manager = manager;
    }

    public void setReplenish(@NotNull Player target, boolean replenish) {
        if (!replenish) {
            target.removeMetadata("fakeplayer:replenish", Main.getInstance());
        } else {
            target.setMetadata("fakeplayer:replenish", new FixedMetadataValue(Main.getInstance(), true));
        }

    }

    public boolean isReplenish(@NotNull Player target) {
        return target.hasMetadata("fakeplayer:replenish");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemUse(@NotNull PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();

        if (this.isReplenish(player)) {
            EquipmentSlot slot = event.getHand();
            ItemStack item = player.getInventory().getItem(slot);

            if (item.getAmount() == 1) {
                this.replenishLater(player, slot, item);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(@NotNull BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (this.isReplenish(player)) {
            EquipmentSlot slot = event.getHand();
            ItemStack item = player.getInventory().getItem(slot);

            if (item.getAmount() == 1) {
                this.replenishLater(player, slot, item);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onItemBreak(@NotNull PlayerItemBreakEvent event) {
        Player player = event.getPlayer();

        if (this.isReplenish(player)) {
            ItemStack item = event.getBrokenItem();
            EquipmentSlot slot = this.getHoldingHand(player, item);

            if (slot != null) {
                this.replenishLater(player, slot, item);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(@NotNull PlayerLaunchProjectileEvent event) {
        Player player = event.getPlayer();

        if (this.isReplenish(event.getPlayer())) {
            ItemStack item = event.getItemStack();

            if (item.getAmount() == 1) {
                EquipmentSlot slot = this.getHoldingHand(player, item);

                if (slot != null) {
                    this.replenishLater(player, slot, item);
                }
            }
        }
    }

    public void replenishLater(@NotNull Player target, @NotNull EquipmentSlot slot, @NotNull ItemStack item) {
        ItemStack requires = item.clone();
        Object object = null;

        Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
            if (target.isOnline()) {
                ItemStack held = target.getInventory().getItem(slot);

                if (held.getType().isAir() || held.getAmount() == 0) {
                    if (!this.replenishFromInventory(target, slot, requires) && Optional.ofNullable(this.manager.getCreator(target)).filter((creatorx) -> {
                        return creatorx.hasPermission("cactusbot.config.replenish.chest");
                    }).isPresent()) {
                        this.replenishFromNearbyChest(target, slot, requires);
                    }

                }
            }
        }, 1L);
    }

    private boolean replenishFromInventory(@NotNull Player target, @NotNull EquipmentSlot slot, @NotNull ItemStack item) {
        PlayerInventory inv = target.getInventory();

        for (int i = inv.getSize() - 1; i >= 0; --i) {
            ItemStack replacement = inv.getItem(i);

            if (replacement != null && replacement.isSimilar(item)) {
                inv.setItem(slot, replacement);
                inv.setItem(i, (ItemStack) null);
                return true;
            }
        }

        return false;
    }

    public void replenishFromNearbyChest(@NotNull Player target, @NotNull EquipmentSlot slot, @NotNull ItemStack item) {
        for (Block block : BlockUtils.getNearbyBlocks(target.getLocation(), 4, Material.CHEST)) {
            PlayerInteractEvent openEvent = new PlayerInteractEvent(target, Action.RIGHT_CLICK_BLOCK, target.getInventory().getItemInOffHand(), block, BlockFace.NORTH);

            if (openEvent.callEvent() && target.openInventory(((Chest) block.getState()).getBlockInventory()) != null) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                    InventoryView view = target.getOpenInventory();
                    Inventory inv = view.getTopInventory();

                    if (inv.getType() == InventoryType.CHEST) {
                        for (int i = inv.getSize() - 1; i >= 0; --i) {
                            ItemStack replacement = inv.getItem(i);

                            if (replacement != null && replacement.isSimilar(item)) {
                                InventoryClickEvent event = new InventoryClickEvent(view, SlotType.CONTAINER, i, ClickType.SHIFT_LEFT, InventoryAction.MOVE_TO_OTHER_INVENTORY);

                                if (event.callEvent()) {
                                    target.getInventory().setItem(slot, replacement);
                                    inv.setItem(i, (ItemStack) null);
                                }
                                break;
                            }
                        }

                        target.closeInventory(Reason.PLAYER);
                    }
                }, 20L);
                return;
            }
        }

    }

    private @Nullable EquipmentSlot getHoldingHand(@NotNull Player player, @NotNull ItemStack item) {
        PlayerInventory inv = player.getInventory();

        return item.equals(inv.getItemInMainHand()) ? EquipmentSlot.HAND : (item.equals(inv.getItemInOffHand()) ? EquipmentSlot.OFF_HAND : null);
    }
}
