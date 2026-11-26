package io.github.hello09x.fakeplayer.v1_21_11.spi;

import com.mojang.authlib.GameProfile;
import io.github.hello09x.devtools.core.utils.WorldUtils;
import io.github.hello09x.fakeplayer.api.spi.NMSServer;
import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_21_R7.CraftServer;
import org.jetbrains.annotations.NotNull;

public class NMSServerImpl implements NMSServer {
   private final MinecraftServer handle;

   public NMSServerImpl(@NotNull Server server) {
      this.handle = ((CraftServer)server).getServer();
   }

   @NotNull
   @Override
   public NMSServerPlayer newPlayer(@NotNull UUID uuid, @NotNull String name) {
      MinecraftServer server = new NMSServerImpl(Bukkit.getServer()).getHandle();
      EntityPlayer handle = new EntityPlayer(
         new NMSServerImpl(Bukkit.getServer()).getHandle(),
         new NMSServerLevelImpl(WorldUtils.getMainWorld()).getHandle(),
         new GameProfile(uuid, name),
         ClientInformation.a()
      );
      server.aj().s.a(handle.gJ()).ifPresent(nbt -> {
         ValueInput valueInput = TagValueInput.a(ProblemReporter.a, server.bc(), nbt);
         handle.d(valueInput);
      });
      return new NMSServerPlayerImpl(handle.getBukkitEntity());
   }

   @Generated
   public MinecraftServer getHandle() {
      return this.handle;
   }
}
