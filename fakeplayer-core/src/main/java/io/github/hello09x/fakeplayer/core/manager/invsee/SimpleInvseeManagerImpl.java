package io.github.hello09x.fakeplayer.core.manager.invsee;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.hello09x.devtools.core.utils.ComponentUtils;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerList;
import io.github.hello09x.fakeplayer.core.manager.FakeplayerManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Singleton
public class SimpleInvseeManagerImpl extends AbstractInvseeManager {
   @Inject
   public SimpleInvseeManagerImpl(FakeplayerManager manager, FakeplayerList fakeplayerList) {
      super(manager, fakeplayerList);
   }

   @Nullable
   @Override
   protected InventoryView openInventory(@NotNull Player viewer, @NotNull Player whom) {
      InventoryView view = viewer.openInventory(whom.getInventory());
      if (view != null) {
         view.setTitle(
            ComponentUtils.toString(
               Component.translatable("fakeplayer.manager.inventory.title", new ComponentLike[]{Component.text(whom.getName())}), viewer.locale()
            )
         );
      }

      return view;
   }
}
