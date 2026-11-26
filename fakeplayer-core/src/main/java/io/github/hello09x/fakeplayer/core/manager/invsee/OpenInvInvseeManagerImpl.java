package io.github.hello09x.fakeplayer.core.manager.invsee;

import com.google.common.base.Throwables;
import com.lishid.openinv.IOpenInv;
import io.github.hello09x.fakeplayer.core.Main;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerList;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;

public class OpenInvInvseeManagerImpl extends AbstractInvseeManager {
   private static final Logger log = Main.getInstance().getLogger();
   private final IOpenInv openInv = (IOpenInv)Bukkit.getPluginManager().getPlugin("OpenInv");

   public OpenInvInvseeManagerImpl(FakeplayerManager manager, FakeplayerList fakeplayerList) {
      super(manager, fakeplayerList);
   }

   @Override
   protected InventoryView openInventory(@NotNull Player viewer, @NotNull Player whom) {
      try {
         return this.openInv.openInventory(viewer, this.openInv.getSpecialInventory(whom, true));
      } catch (InstantiationException var4) {
         log.warning("Failed to %s's open inventory for %s\n%s".formatted(whom.getName(), viewer.getName(), Throwables.getStackTraceAsString(var4)));
         return null;
      }
   }
}
