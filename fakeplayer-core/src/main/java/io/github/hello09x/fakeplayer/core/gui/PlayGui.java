package io.github.hello09x.fakeplayer.core.gui;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.fakeplayer.core.config.FakeplayerConfig;
import io.github.hello09x.fakeplayer.core.config.GamemodeDefinition;
import io.github.hello09x.fakeplayer.core.manager.pvp.QueueManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

@Singleton
public class PlayGui implements Listener {
   private final FakeplayerConfig config;
   private final QueueManager queueManager;
   private final Map<Inventory, Map<Integer, String>> mappings = new HashMap<>();

   @Inject
   public PlayGui(FakeplayerConfig config, QueueManager queueManager) {
      this.config = config;
      this.queueManager = queueManager;
   }

   public void open(@NotNull Player player) {
      ArrayList<GamemodeDefinition> gms = new ArrayList<>(this.config.getGamemodes().values());
      gms.sort((a, b) -> a.name().compareToIgnoreCase(b.name()));
      int size = Math.max(9, (gms.size() + 8) / 9 * 9);
      Inventory inv = Bukkit.createInventory(player, size, Component.text(this.config.getPlayGuiTitle(), NamedTextColor.GOLD));
      HashMap<Integer, String> map = new HashMap<>();

      for (int i = 0; i < gms.size() && i < size; i++) {
         GamemodeDefinition gm = gms.get(i);
         Material mat = gm.icon() == null ? Material.PAPER : gm.icon();
         ItemStack item = new ItemStack(mat);
         ItemMeta meta = item.getItemMeta();
         meta.displayName(Component.text(gm.name(), gm.displayColor()));
         item.setItemMeta(meta);
         inv.setItem(i, item);
         map.put(i, gm.name());
      }

      this.mappings.put(inv, map);
      player.openInventory(inv);
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.HIGHEST
   )
   public void onClick(@NotNull InventoryClickEvent event) {
      Inventory inv = event.getInventory();
      Map<Integer, String> map = this.mappings.get(inv);
      if (map != null) {
         event.setCancelled(true);
         if (event.getWhoClicked() instanceof Player player) {
            int var7 = event.getRawSlot();
            String gamemode = map.get(var7);
            if (gamemode != null) {
               this.queueManager.queue(player, gamemode);
               player.closeInventory();
            }
         }
      }
   }
}
