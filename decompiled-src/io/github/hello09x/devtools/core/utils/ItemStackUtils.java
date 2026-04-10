package io.github.hello09x.devtools.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ItemStackUtils {

    public ItemStackUtils() {
        super();
    }

    public static void appendLore(@NotNull ItemStack itemStack, @NotNull List<? extends Component> lore) {
        itemStack.editMeta((metax) -> {
            ArrayList<Component> current = new ArrayList((Collection) Optional.ofNullable(metax.lore()).orElseGet(ArrayList::new));

            current.addAll(lore);
            metax.lore(lore);
        });
    }

    public static void appendLore(@NotNull ItemStack itemStack, Component... lore) {
        appendLore(itemStack, Arrays.asList(lore));
    }
}
