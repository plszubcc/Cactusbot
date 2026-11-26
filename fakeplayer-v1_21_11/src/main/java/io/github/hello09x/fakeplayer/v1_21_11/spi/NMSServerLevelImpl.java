package io.github.hello09x.fakeplayer.v1_21_11.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSServerLevel;
import lombok.Generated;
import net.minecraft.server.level.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R7.CraftWorld;
import org.jetbrains.annotations.NotNull;

public class NMSServerLevelImpl implements NMSServerLevel {
   private final WorldServer handle;

   public NMSServerLevelImpl(@NotNull World world) {
      this.handle = ((CraftWorld)world).getHandle();
   }

   @Generated
   public WorldServer getHandle() {
      return this.handle;
   }
}
