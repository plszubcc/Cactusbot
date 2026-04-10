package io.github.hello09x.devtools.core.utils;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InventoryUtils {

    public InventoryUtils() {
        super();
    }

    public static int removeItem(@NotNull Inventory inventory, @NotNull ItemStack item, int max) {
        int removed = 0;
        ListIterator listiterator = inventory.iterator();

        while (listiterator.hasNext()) {
            ItemStack each = (ItemStack) listiterator.next();

            if (removed == max) {
                return removed;
            }

            if (each != null && each.asOne().equals(item)) {
                int delta = Math.min(each.getAmount(), max - removed);

                each.subtract(delta);
                removed += delta;
            }
        }

        return removed;
    }

    public static void addItem(@NotNull Player player, @NotNull ItemStack item) {
        HashMap<Integer, ItemStack> result = player.getInventory().addItem(new ItemStack[]{item});

        if (!result.isEmpty()) {
            for (ItemStack unfit : result.values()) {
                player.getWorld().dropItem(player.getLocation(), unfit);
            }
        }

    }

    public static void replace(@NotNull Inventory replaceTo, @NotNull Map<Integer, ItemStack> replacements, boolean clear) {
        for (int i = replaceTo.getSize() - 1; i >= 0; --i) {
            ItemStack replacement = (ItemStack) replacements.get(i);

            if (replacement != null) {
                replaceTo.setItem(i, replacement);
            } else if (clear) {
                replaceTo.setItem(i, (ItemStack) null);
            }
        }

    }

    public static @NotNull Map<Integer, ItemStack> toMap(@NotNull Inventory inventory) {
        HashMap<Integer, ItemStack> items = new HashMap(inventory.getSize(), 1.0F);
        ListIterator<ItemStack> itr = inventory.iterator();

        while (itr.hasNext()) {
            int i = itr.nextIndex();
            ItemStack item = (ItemStack) itr.next();

            if (item != null) {
                items.put(i, item);
            }
        }

        return items;
    }

    public static boolean moveItem(@NotNull Inventory from, int slot, @NotNull Inventory to) {
        if (copyItem(from, slot, to)) {
            from.setItem(slot, (ItemStack) null);
            return true;
        } else {
            return false;
        }
    }

    public static boolean copyItem(@NotNull Inventory from, int slot, @NotNull Inventory to) {
        int target = to.firstEmpty();

        if (target == -1) {
            return false;
        } else {
            ItemStack item = from.getItem(slot);

            if (item == null) {
                return false;
            } else {
                to.setItem(target, item);
                return true;
            }
        }
    }
}
