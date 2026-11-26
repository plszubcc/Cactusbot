package io.github.hello09x.fakeplayer.v1_21_11.spi;

import io.github.hello09x.fakeplayer.api.spi.NMSServerPlayer;
import io.github.hello09x.fakeplayer.core.util.Reflections;
import io.github.hello09x.fakeplayer.v1_21_11.network.FakePlayerAdvancements;
import java.lang.reflect.Field;
import java.util.stream.Stream;
import lombok.Generated;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.EnumDirection;
import net.minecraft.core.HolderLookup.a;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig;
import net.minecraft.network.protocol.game.PacketPlayInClientCommand;
import net.minecraft.network.protocol.game.PacketPlayInBlockDig.EnumPlayerDigType;
import net.minecraft.network.protocol.game.PacketPlayInClientCommand.EnumClientCommand;
import net.minecraft.server.AdvancementDataPlayer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.ParticleStatus;
import net.minecraft.world.entity.EnumMainHand;
import net.minecraft.world.entity.player.EnumChatVisibility;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.level.storage.ValueInputContextHelper;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_21_R7.CraftServer;
import org.bukkit.craftbukkit.v1_21_R7.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class NMSServerPlayerImpl implements NMSServerPlayer {
   private static final Field ServerPlayer$advancements = Reflections.getFirstFieldByType(EntityPlayer.class, AdvancementDataPlayer.class, false);
   private final EntityPlayer handle;
   private final CraftPlayer player;

   public NMSServerPlayerImpl(@NotNull Player player) {
      this.player = (CraftPlayer)player;
      this.handle = ((CraftPlayer)player).getHandle();
   }

   @Override
   public double getX() {
      return this.handle.dP();
   }

   @Override
   public double getY() {
      return this.handle.dR();
   }

   @Override
   public double getZ() {
      return this.handle.dV();
   }

   @Override
   public void setXo(double xo) {
      this.handle.Y = xo;
   }

   @Override
   public void setYo(double yo) {
      this.handle.Z = yo;
   }

   @Override
   public void setZo(double zo) {
      this.handle.aa = zo;
   }

   @Override
   public void doTick() {
      this.handle.h();
   }

   @Override
   public void absMoveTo(double x, double y, double z, float yRot, float xRot) {
      this.handle.a(x, y, z, yRot, xRot);
   }

   @Override
   public float getYRot() {
      return this.handle.ec();
   }

   @Override
   public void setYRot(float yRot) {
      this.handle.v(yRot);
   }

   @Override
   public float getXRot() {
      return this.handle.ee();
   }

   @Override
   public void setXRot(float xRot) {
      this.handle.w(xRot);
   }

   @Override
   public float getZza() {
      return this.handle.bP;
   }

   @Override
   public void setZza(float zza) {
      this.handle.bP = zza;
   }

   @Override
   public float getXxa() {
      return this.handle.bN;
   }

   @Override
   public void setXxa(float xxa) {
      this.handle.bN = xxa;
   }

   @Override
   public void setDeltaMovement(@NotNull Vector vector) {
      this.handle.k(new Vec3D(vector.getX(), vector.getY(), vector.getZ()));
   }

   @Override
   public boolean startRiding(@NotNull Entity entity, boolean force) {
      return this.handle.a(new NMSEntityImpl(entity).getHandle(), force, true);
   }

   @Override
   public void stopRiding() {
      this.handle.cf();
   }

   @Override
   public int getTickCount() {
      return this.handle.at;
   }

   @Override
   public void drop(boolean allStack) {
      this.handle.a(allStack);
   }

   @Override
   public void resetLastActionTime() {
      this.handle.J();
   }

   @Override
   public boolean onGround() {
      return this.handle.aV();
   }

   @Override
   public void jumpFromGround() {
      this.handle.u();
   }

   @Override
   public void setJumping(boolean jumping) {
      this.handle.t(jumping);
   }

   @Override
   public boolean isUsingItem() {
      return this.handle.fZ();
   }

   @Override
   public void disableAdvancements(@NotNull Plugin plugin) {
      if (ServerPlayer$advancements != null) {
         DedicatedServer server = ((CraftServer)Bukkit.getServer()).getServer();

         try {
            ServerPlayer$advancements.set(
               this.handle, new FakePlayerAdvancements(server.aA(), server.bA(), server.aB(), plugin.getDataFolder().getParentFile().toPath(), this.handle)
            );
         } catch (IllegalAccessException var4) {
         }
      }
   }

   @Override
   public void drop(int slot, boolean flag, boolean flag1) {
      PlayerInventory inventory = this.handle.gK();
      this.handle.a(inventory.a(slot, inventory.a(slot).N()), flag, flag1);
   }

   @Override
   public void setPlayBefore() {
      this.player.readExtraData(new ValueInputContextHelper(a.a(Stream.empty()), null).c());
   }

   @Override
   public void setupClientOptions() {
      ClientInformation option = new ClientInformation(
         "en_us", Bukkit.getViewDistance(), EnumChatVisibility.b, false, 127, EnumMainHand.b, false, true, ParticleStatus.c
      );
      this.handle.a(option);
   }

   @Override
   public void respawn() {
      if (this.player.isDead()) {
         PacketPlayInClientCommand packet = new PacketPlayInClientCommand(EnumClientCommand.a);
         this.handle.g.a(packet);
      }
   }

   @Override
   public void swapItemWithOffhand() {
      this.handle.g.a(new PacketPlayInBlockDig(EnumPlayerDigType.g, new BlockPosition(0, 0, 0), EnumDirection.a));
   }

   @Override
   public void startFallFlying() {
      this.handle.gS();
   }

   @Generated
   public EntityPlayer getHandle() {
      return this.handle;
   }

   @Generated
   public CraftPlayer getPlayer() {
      return this.player;
   }
}
